package answer.king.service;

import answer.king.entity.Account;
import answer.king.error.AccountAlreadyExistsException;
import answer.king.error.InvalidCriteriaException;
import answer.king.error.NotFoundException;
import answer.king.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(String username, String password) throws AccountAlreadyExistsException, InvalidCriteriaException {
        if (username == null || password == null) {
            throw new InvalidCriteriaException();
        }

        // check if user exists
        final Account account = accountRepository.findByUsername(username);
        if (account != null) throw new AccountAlreadyExistsException();

        password = passwordEncoder.encode(password);

        return accountRepository.save(new Account(username, password));
    }

    public Account get(String username) throws NotFoundException {
        if (username == null) throw new NullPointerException();

        final Account account = accountRepository.findByUsername(username);
        if (account == null) throw new NotFoundException();
        return account;
    }
}
