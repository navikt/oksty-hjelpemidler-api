package no.navn.oebs.hjelpemiddel.api;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJwtTokenValidation(ignore = {"org.springdoc", "org.springframework"})
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "BearerAuth")
public class HjelpemiddelApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HjelpemiddelApiApplication.class, args);
	}

}
