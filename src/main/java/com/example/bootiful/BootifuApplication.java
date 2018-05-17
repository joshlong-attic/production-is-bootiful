package com.example.bootiful;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class BootifuApplication {

		@Bean
		MapReactiveUserDetailsService users() {
				return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder().username("user").password("pw").roles("USER").build());
		}

		@Bean
		HealthIndicator healthIndicator() {
				return () -> Health.status("I <3 Production!").build();
		}

		@Bean
		RouterFunction<ServerResponse> routes(CustomerRepository cr) {
				return route(GET("/customers"), serverRequest -> ok().body(cr.findAll(), Customer.class));
		}

		public static void main(String[] args) {
				SpringApplication.run(BootifuApplication.class, args);
		}
}

@Component
class DataWriter implements ApplicationRunner {

		private final CustomerRepository customerRepository;

		DataWriter(CustomerRepository customerRepository) {
				this.customerRepository = customerRepository;
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {

				Flux.just("Mia", "Madhura", "Dave", "Onsi")
					.flatMap(name -> customerRepository.save(new Customer(null, name)))
					.subscribe(System.out::println);
		}
}

interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
		private String id, name;
}