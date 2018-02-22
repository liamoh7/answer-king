package answer.king.config;

import answer.king.service.AuthenticationService;
import com.google.common.base.Predicate;
import org.dozer.DozerBeanMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class BeanConfig {

    @Bean
    public CommandLineRunner init(AuthenticationService service) {
        return args -> service.createAccount("liam", "password");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DozerBeanMapper modelMapper() {
        return new DozerBeanMapper();
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        Predicate<String> apiPaths = or(regex("/order.*"), regex("/item.*"),
                (regex("/receipt.*")), (regex("/categories.*")));
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(apiPaths)
                .build();
    }
}
