package account.handlers;

import account.controllers.SecurityEventController;
import account.models.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private SecurityEventController securityEventController;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError( 403, "Access Denied!");

        securityEventController.signalEvent(Event.ACCESS_DENIED,
                SecurityContextHolder.getContext().getAuthentication().getName(),
                request.getRequestURI(),
                request.getRequestURI());
    }
}
