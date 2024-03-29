package account.configuration;

import account.controllers.SecurityEventController;
import account.models.entities.User;
import account.models.Event;
import account.repositories.UserRepository;
import account.services.AuthenticationFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private SecurityEventController securityEventController;

    @Autowired
    private AuthenticationFailureService authenticationFailureService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        String header = request.getHeader("authorization");
        if (header != null) {
            header = header.split("\\s+")[1];
            header = new String(Base64.getDecoder().decode(header)).split(":")[0];

            User user = userRepository.findByEmailIgnoreCase(header);
            if (user == null || user.isAccountNonLocked()) {
                securityEventController.signalEvent(Event.LOGIN_FAILED,
                        header,
                        request.getRequestURI(),
                        request.getRequestURI());
            }
            if (user != null && user.isAccountNonLocked()) {
                authenticationFailureService.increaseFailedAttempts(user, request.getRequestURI());
            }
        }
    }


}
