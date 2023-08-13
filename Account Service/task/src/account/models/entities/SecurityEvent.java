package account.models.entities;

import account.models.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Table(name = "securityEvents")
@Entity
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column
    private Long securityEventId;
    @Column
    private LocalDate date;
    @Column
    private Event action;
    @Column
    private String subject;
    @Column
    private String object;
    @Column
    private String path;

    public SecurityEvent(LocalDate date, Event action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
