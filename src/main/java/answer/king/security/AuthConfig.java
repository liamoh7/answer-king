package answer.king.security;

import answer.king.entity.Account;
import answer.king.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AuthConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) {
        try {
            auth.userDetailsService(userDetailsService());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            final Account account = accountRepository.findByUsername(username);
            if (account == null) throw new UsernameNotFoundException("Could not find user " + username);

            return new User(account.getUsername(), account.getPassword(), true, true,
                    true, true, AuthorityUtils.createAuthorityList("USER"));
        };
    }
}
