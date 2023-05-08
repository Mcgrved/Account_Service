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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

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
            Logger.getGlobal().info("User: " + user);
            Logger.getGlobal().info("Header: " + header);
            if (user == null || user.isAccountNonLocked()) {
                securityEventController.signalEvent(Event.LOGIN_FAILED,
                        header,
                        request.getRequestURI(),
                        request.getRequestURI());
            }
            if (user != null && user.isAccountNonLocked()) {
              // Logger.getGlobal().info("FAiled Login: " + user.getFailedAttempts());
                authenticationFailureService.increaseFailedAttempts(user, request.getRequestURI());
            }
        }
    }
}
