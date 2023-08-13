package account.services;

import account.controllers.SecurityEventController;
import account.models.entities.Payment;
import account.models.entities.User;
import account.models.Event;
import account.models.responses.UserPaymentResponse;
import account.repositories.PaymentRepository;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

@Service
public class BusinessService {

    @Autowired
    private SecurityEventController securityEventController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public ResponseEntity<?> getPayment(UserDetails userDetails, String period) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        if (user != null) {
            if (period != null) {
                 if (period.substring(0,2).matches("(0[1-9])|(1[0-2])")) {
                     Payment payment = paymentRepository.findOneByEmployeeIgnoreCaseAndPeriod(user.getEmail(),
                             YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy")));
                     return new ResponseEntity<>(buildResponse(user, payment), HttpStatus.OK);
                 } else {
                     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                             "Bad Request");
                 }

            } else {
                Payment[] payments = paymentRepository.findByEmployeeIgnoreCase(user.getEmail());
                if (payments != null) {
                    return new ResponseEntity<>(buildResponseArray(user, payments), HttpStatus.OK);
                }
            }
        }
        securityEventController.signalEvent(Event.LOGIN_FAILED,
                SecurityContextHolder.getContext().getAuthentication().getName(),
                "/api/empl/payment",
                "/api/empl/payment");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Bad Request");
    }


    @Transactional
    public ResponseEntity<?> postPayments(Payment[] payments) {
        if (payments != null) {
            for (Payment p : payments) {
                if (p.getSalary() > 1) {
                    savePaymentTransactional(p);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Wrong salary in payment list");
                }
            }
            return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<?> putPayment(Payment payment) {
        User user = userRepository.findByEmailIgnoreCase(payment.getEmployee());
        if (user != null) {
            Payment foundPayment = paymentRepository.findOneByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod());
            foundPayment.setSalary(payment.getSalary());
            paymentRepository.save(foundPayment);
            return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    private void savePaymentTransactional(Payment p) {
        Payment[] findArr = paymentRepository.findByEmployeeIgnoreCaseAndPeriod(p.getEmployee(), p.getPeriod());
        if (findArr.length != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User duplicate exception");
        } else {
            paymentRepository.save(p);
        }
    }

    private UserPaymentResponse[] buildResponseArray(User user, Payment[] payments) {
        UserPaymentResponse[] userPaymentResponse = new UserPaymentResponse[payments.length];
        for (int i = 0; i < payments.length; i++) {
            userPaymentResponse[i] = buildResponse(user, payments[i]);
        }
        return Arrays.stream(userPaymentResponse)
                .sorted(Comparator.comparing(UserPaymentResponse::getPeriod).reversed())
                .toArray(UserPaymentResponse[]::new);
    }

    private UserPaymentResponse buildResponse(User user, Payment payment) {
        return new UserPaymentResponse(user.getName(), user.getLastname(),
                payment.getPeriod(), payment.getSalary());
    }


}
