package account.services;

import account.controllers.SecurityEventController;
import account.models.entities.Role;
import account.models.Event;
import account.models.requests.AccessRequest;
import account.models.requests.RoleRequest;
import account.models.entities.User;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class FunctionalityService {

    @Autowired
    private SecurityEventController securityEventController;

    @Autowired
    private AuthenticationFailureService authenticationFailureService;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> changeRole(RoleRequest roleRequest) {
        User foundUser = userRepository.findByEmailIgnoreCase(roleRequest.getUser());
        if (foundUser != null) {
            roleOperationInvoke(foundUser, roleRequest);
            userRepository.save(foundUser);

            Event role = roleRequest.getOperation().equals("GRANT") ? Event.GRANT_ROLE : Event.REMOVE_ROLE;
            String signalOp = roleRequest.getOperation().equals("GRANT") ? "Grant " : "Remove ";
            String str = roleRequest.getOperation().equals("GRANT") ? " to " : " from ";
            securityEventController.signalEvent(role,
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    signalOp + "role " + roleRequest.getRole() +
                            str + roleRequest.getUser().toLowerCase(Locale.ROOT),
                    "/api/admin/user/role");
            return ResponseEntity.ok(foundUser);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }

    public ResponseEntity<?> deleteUser(String email) {
        User foundUser = userRepository.findByEmailIgnoreCase(email);
        if (foundUser != null) {
            if (!foundUser.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                userRepository.delete(foundUser);
                securityEventController.signalEvent(Event.DELETE_USER,
                        SecurityContextHolder.getContext().getAuthentication().getName(),
                        foundUser.getEmail(),
                        "/api/admin/user");
                return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't remove ADMINISTRATOR role!");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found!");
    }

    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    private void roleOperationInvoke(User user, RoleRequest request) {
            Role role = findRole(request.getRole());
            if (role != null) {
                switch (request.getOperation()) {
                    case "GRANT":
                        if (checkIfValid(user.getRoles(), request.getOperation(), role)) {
                            user.addRole(role);
                        }
                        break;
                    case "REMOVE":
                        if (checkIfValid(user.getRoles(), request.getOperation(), role)) {
                            user.getRoles().remove(role);
                        }
                        break;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
            }
    }

    private boolean checkIfValid(ArrayList<Role> userRoles, String operation, Role requestRole) {
        if (checkIfContainsBusinessRole(userRoles) != requestRole.isBusinessUser()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
        if (!userRoles.contains(requestRole) && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user does not have a role!");
        }
        if (userRoles.contains(Role.ROLE_ADMINISTRATOR) && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't remove ADMINISTRATOR role!");
        }
        if (userRoles.size() < 2 && operation.equals("REMOVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user must have at least one role!");
        }
        return true;
    }

    private Role findRole(String roleName) {
        for (Role r : Role.values()) {
            if (Objects.equals(r.getName(), roleName)) {
                return r;
            }
        }
        return null;
    }

    private boolean checkIfContainsBusinessRole(ArrayList<Role> roles) {
         return roles.stream()
                 .map(Role::isBusinessUser)
                 .filter(x -> x)
                 .findFirst()
                 .orElse(false);
    }

    public ResponseEntity<?> putAccess(AccessRequest accessRequest) {
        User user = userRepository.findByEmailIgnoreCase(accessRequest.getUser());
        if (user != null) {
            String operation = "";
            if (accessRequest.getOperation().equals("LOCK")
                    && user.isAccountNonLocked()
                    && !user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                authenticationFailureService.lockUser(user);
                securityEventController.signalEvent(Event.LOCK_USER,
                        SecurityContextHolder.getContext().getAuthentication().getName(),
                        "Lock user " + user.getEmail(),
                        "/api/admin/user/access");
                operation = " locked!";
                return ResponseEntity.ok(Map.of("status", "User " + user.getEmail() + operation));
            } else if (accessRequest.getOperation().equals("UNLOCK") && !user.isAccountNonLocked()){
                authenticationFailureService.unlockUser(user);
                securityEventController.signalEvent(Event.UNLOCK_USER,
                        SecurityContextHolder.getContext().getAuthentication().getName(),
                        "Unlock user " + user.getEmail(),
                        "/api/admin/user/access");
                operation = " unlocked!";
                return ResponseEntity.ok(Map.of("status", "User " + user.getEmail() + operation));
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Can't lock the ADMINISTRATOR!");
    }
}


