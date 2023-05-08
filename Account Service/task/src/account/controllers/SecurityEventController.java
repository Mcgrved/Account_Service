package account.controllers;

import account.models.Event;
import account.services.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecurityEventController {

    private final SecurityEventService securityEventService;

    @Autowired
    public SecurityEventController(SecurityEventService securityEventService) {
        this.securityEventService = securityEventService;
    }

    @GetMapping("/security/events")
    public ResponseEntity<?> getSecurityEvents() {
        return securityEventService.getSecurityEvents();
    }

    public ResponseEntity<?> signalEvent(Event event, String subject, String object, String path) {
        return this.securityEventService.signalEvent(event, subject, object, path);
    }
}
