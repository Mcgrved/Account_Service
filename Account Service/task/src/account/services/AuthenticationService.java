package account.services;

import account.controllers.SecurityEventController;
import account.exceptions.PasswordHackedException;
import account.exceptions.UserExistsException;
import account.models.Event;
import account.models.requests.NewPassword;
import account.models.entities.Role;
import account.models.entities.User;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Validated
public class AuthenticationService {

    @Autowired
    private SecurityEventController securityEventController;

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private String[] breachedPasswords;


    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> postUser(User user) {
        if (userRepository.findByNameAndLastname(user.getName(), user.getLastname()) != null) {
            throw new UserExistsException();
        }
        securityEventController.signalEvent(Event.CREATE_USER, "Anonymous",
                user.getEmail(), "api/auth/signup");
        giveFirstRoles(user);
        checkBreachedPassword(user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        Logger.getGlobal().info("User " + user.getRoles());
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<?> changePass(UserDetails userDetails, NewPassword newPassword) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        if (user != null) {
            checkPasswordAll(newPassword.getNewPassword(), user.getPassword());
            user.setPassword(encoder.encode(newPassword.getNewPassword()));
            userRepository.save(user);
            securityEventController.signalEvent(Event.CHANGE_PASSWORD,
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    user.getEmail(),
                    "/api/auth/changepass");
            return new ResponseEntity<Map<String, String>>(Map.of("email", user.getEmail().toLowerCase(Locale.ROOT),
                    "status", "The password has been updated successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private void checkPasswordAll(String password, String oldHashedPassword) {
        checkBreachedPassword(password);
        checkPasswordLength(password);
        checkSamePassword(password, oldHashedPassword);
    }

    private void checkBreachedPassword(String password) {
        for (String breachedPassword : breachedPasswords) {
            if (encoder.matches(password, breachedPassword)) {
                throw new PasswordHackedException();
            }
        }
    }

    private void checkSamePassword(String password, String oldHashedPassword) {
        if (encoder.matches(password, oldHashedPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The passwords must be different!");
        }
    }

    private void checkPasswordLength(String password) {
        if (password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password length must be 12 chars minimum!");
        }
    }

    private boolean checkIfFirstUser() {
        ArrayList<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users.size() == 0;
    }

    private void giveFirstRoles(User user) {
        if (checkIfFirstUser()) {
            user.getRoles().add(Role.ROLE_ADMINISTRATOR);
        } else {
            user.getRoles().add(Role.ROLE_USER);
        }
    }


}
