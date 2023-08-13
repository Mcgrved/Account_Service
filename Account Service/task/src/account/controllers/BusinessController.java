package account.controllers;

import account.models.entities.Payment;
import account.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api")
public class BusinessController {

    private final BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam(name = "period", required = false) String period) {
        return businessService.getPayment(userDetails, period);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> postPayments(@Valid @RequestBody Payment[] payments) {
       return businessService.postPayments(payments);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<?> putPayment(@Valid @RequestBody Payment payment) {
      return businessService.putPayment(payment);
    }




}
