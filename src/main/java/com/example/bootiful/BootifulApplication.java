package com.example.bootiful;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

@SpringBootApplication
public class BootifulApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootifulApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerRepository repository) {
        return args ->
                Stream.of("Cornelia", "Mia", "Dave", "Onsi")
                        .forEach(x -> repository.save(new Customer(null, x)));
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(Collections.singleton(User.withUsername("rwinch").password("pw")
                .roles("ADMIN").build()));
    }
}

@Component
class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.status("I <3 Production!").build();
    }
}

@RestController
class CustomerRestController {
    private final CustomerRepository customerRepository;

    CustomerRestController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.customerRepository.findAll();
    }
}


interface CustomerRepository extends JpaRepository<Customer, Long> {
}

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}