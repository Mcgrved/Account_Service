package account.controllers;

import account.models.requests.AccessRequest;
import account.models.requests.RoleRequest;
import account.services.FunctionalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class FunctionalityController {

    private final FunctionalityService functionalityService;

    @Autowired
    public FunctionalityController(FunctionalityService functionalityService) {
        this.functionalityService = functionalityService;
    }

    @PutMapping("/role")
    public ResponseEntity<?> changeRole(@RequestBody RoleRequest roleRequest) {
        return functionalityService.changeRole(roleRequest);
    }

    @DeleteMapping("{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return functionalityService.deleteUser(email);
    }

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return functionalityService.getUsers();
    }

    @PutMapping("/access")
    public ResponseEntity<?> putAccess(@RequestBody AccessRequest accessRequest) {
        return functionalityService.putAccess(accessRequest);
    }

}
