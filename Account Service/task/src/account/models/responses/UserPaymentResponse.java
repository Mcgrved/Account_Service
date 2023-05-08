package account.models.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentResponse {
    private String name;
    private String lastname;
    @JsonFormat(pattern = "MMMM-yyyy", locale = "en")
    private YearMonth period;
    private String salary;

    public UserPaymentResponse(String name, String lastname, YearMonth period, Long salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = processSalaryToString(salary);
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public void setSalary(Long salary) {
        this.salary = processSalaryToString(salary);
    }

    private String processSalaryToString(Long salary) {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }
}
