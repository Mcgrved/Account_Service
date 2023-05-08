package account.configuration;

import account.controllers.SecurityEventController;
import account.models.entities.User;
import account.repositories.UserRepository;
import account.services.AuthenticationFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;


public class CustomAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private AuthenticationFailureService authenticationFailureService;

    @Autowired
    private SecurityEventController securityEventController;

    @Autowired
    private UserRepository userRepository;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
        String header = request.getHeader("authorization");
        if (header != null) {
            header = header.split("\\s+")[1];
            header = new String(Base64.getDecoder().decode(header)).split(":")[0];
            User user = userRepository.findByEmailIgnoreCase(header);
            if (user != null) {
                authenticationFailureService.resetFailedAttempts(user);
            }
        }
    }
}
