package account.services;

import account.models.entities.SecurityEvent;
import account.models.Event;
import account.repositories.SecurityEventRepository;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class SecurityEventService {

    @Autowired
    private SecurityEventRepository securityEventRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> getSecurityEvents() {
        List<SecurityEvent> securityEventList = new ArrayList<>(securityEventRepository.findAll());
        return ResponseEntity.ok(securityEventList);
    }

    public void signalEvent(Event event, String subject, String object, String path) {
        SecurityEvent securityEvent = new SecurityEvent(LocalDate.now(),
                event, subject, object, path);
        securityEventRepository.save(securityEvent);
    }


}
