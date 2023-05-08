package account.repositories;


import account.models.entities.Payment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;

@Repository
@EnableJpaRepositories
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Payment findByPaymentId(long id);
    Payment[] findByPeriod(YearMonth period);
    Payment[] findByEmployeeIgnoreCase(String employee);
    Payment[] findByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);
    Payment findOneByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);

}