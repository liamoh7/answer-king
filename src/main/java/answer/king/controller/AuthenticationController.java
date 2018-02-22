package answer.king.controller;

import answer.king.error.AccountAlreadyExistsException;
import answer.king.error.InvalidCriteriaException;
import answer.king.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/register")
    public ResponseEntity createAccount(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) throws AccountAlreadyExistsException, InvalidCriteriaException {
        authenticationService.createAccount(username, password);
        return ResponseEntity.ok().build();
    }
}
