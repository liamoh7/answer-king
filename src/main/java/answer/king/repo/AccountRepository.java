package answer.king.repo;

import answer.king.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // @Query("SELECT account FROM Account account WHERE lower(account.username) = lower(username)")
    Account findByUsername(String username);
}
