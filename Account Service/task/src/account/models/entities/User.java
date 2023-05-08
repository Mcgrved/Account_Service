package account.models.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

@Entity
@Table(name = "users")
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //USER, ACCOUNTANT, ADMINISTRATOR;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")
    @Column
    private long userId;
    @NotEmpty
    @Column
    private String name;
    @NotEmpty
    @Column
    private String lastname;
    @NotEmpty
    @Column
    @Pattern(regexp = "(?i).+@acme\\.[a-z]{3}")
    private String email;
    @NotEmpty
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty("roles")
    @Column
    private ArrayList<Role> roles = new ArrayList<>();
    @JsonIgnore
    @Column
    private int failedAttempts = 0;
    @JsonIgnore
    @Column
    private boolean isAccountNonLocked = true;

    public User(String name, String lastname, String email, String password, ArrayList<Role> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getEmail() {
        return email.toLowerCase(Locale.ROOT);
    }

    public void addRole(Role role) {
        roles.add(role);
        roles.sort(Comparator.comparing(Role::getName));
    }



}
