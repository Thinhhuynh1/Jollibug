package vn.fastfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;

// @SpringBootApplication
//Disable tam spring security
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class,
		ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class })
public class FastFoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastFoodApplication.class, args);
	}

}
