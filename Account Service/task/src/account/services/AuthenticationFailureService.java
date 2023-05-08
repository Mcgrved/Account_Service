package account.services;

import account.controllers.SecurityEventController;
import account.models.entities.Role;
import account.models.entities.User;
import account.models.Event;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFailureService {

    public static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;

    @Autowired
    private SecurityEventController securityEventController;

    @Autowired
    public AuthenticationFailureService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void increaseFailedAttempts(User user, String path) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);

        if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
            lockUser(user);
            securityEventController.signalEvent(Event.BRUTE_FORCE,
                    user.getEmail(),
                    path,
                    path);
            securityEventController.signalEvent(Event.LOCK_USER,
                    user.getEmail(),
                    "Lock user " + user.getEmail(),
                    path);
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    public void lockUser(User user) {
        if (!user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }

    public void unlockUser(User user) {
        user.setAccountNonLocked(true);
        resetFailedAttempts(user);
        userRepository.save(user);
    }
}
