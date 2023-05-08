package account.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@Table(name = "payments")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column
    private Long paymentId;
    @Column
    private String employee;
    @JsonFormat(pattern = "MM-yyyy")
    @Column
    private YearMonth period;
    @Min(1)
    @Column
    private Long salary;

    public Payment(Long paymentId, String employee, YearMonth period, Long salary) {
        this.paymentId = paymentId;
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }
    @Override
    public String toString() {
        return "Payment {" +
                "payrollId=" + paymentId +
                ", employee='" + employee + '\'' +
                ", period='" + period + '\'' +
                ", salary=" + salary +
                '}';
    }
}
