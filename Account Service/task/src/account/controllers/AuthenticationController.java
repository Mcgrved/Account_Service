package account.controllers;


import account.models.requests.NewPassword;
import account.models.entities.User;
import account.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> postUsers(@Valid @RequestBody User user) {
       return authenticationService.postUser(user);
    }

    @PostMapping("/changepass")
    public ResponseEntity<?> changePass(@Valid @RequestBody NewPassword newPassword,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return authenticationService.changePass(userDetails, newPassword);
    }
}
