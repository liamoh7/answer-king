package answer.king.config;

import answer.king.entity.Account;
import answer.king.repo.AccountRepository;
import com.google.common.base.Predicate;
import org.dozer.DozerBeanMapper;
import org.h2.server.web.WebServlet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class BeanConfig {

    @Bean
    public CommandLineRunner init(AccountRepository accountRepository) {
        return args -> accountRepository.save(new Account("liam", "password"));
    }

    @Bean
    public ServletRegistrationBean h2ServletRegistration() {
        return new ServletRegistrationBean(new WebServlet(), "/h2/*");
    }

    @Bean
    public DozerBeanMapper modelMapper() {
        return new DozerBeanMapper();
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        Predicate<String> apiPaths = or(regex("/order.*"), regex("/item.*"), (regex("/receipt.*")));
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(apiPaths)
                .build();
    }
}
