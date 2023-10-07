package com.musicclouds.user;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.security.auth.AuthenticationService;
import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.musicclouds.clients")
@EntityScan(basePackages = {"com.musicclouds.user", "com.musicclouds.security"})
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository,
							 PasswordEncoder passwordEncoder) {
		return args -> {
			var faker = new Faker();
			Name fakerName = faker.name();
			String firstName = fakerName.firstName();
			String lastName = fakerName.lastName();
			String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@music-clouds.com";
			String password = passwordEncoder.encode(UUID.randomUUID().toString());
			String username = fakerName.username();
			Integer age = ThreadLocalRandom.current().nextInt(18, 100);
			Gender gender = ThreadLocalRandom.current().nextInt(100) % 2 == 0 ? Gender.MALE : Gender.FEMALE;

            User user = new User(
					firstName,
					lastName,
					email,
					password,
					username,
					age,
					gender,
                    Role.ADMIN
			);
			userRepository.save(user);
		};
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			try {
				var admin = new UserRegistrationRequest(
						"Admin",
						"Admin",
						"admin@mail.com",
						"password",
						"username890",
						55,
						Gender.MALE,
						Role.ADMIN);
				System.out.println("Admin token: " + service.register(admin).accessToken());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				var manager = new UserRegistrationRequest(
						"Manager",
						"Manager",
						"manager@mail.com",
						"password",
						"username899",
						55,
						Gender.MALE,
						Role.MANAGER
				);
				System.out.println("Manager token: " + service.register(manager).accessToken());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				var user = new UserRegistrationRequest(
						"User",
						"User",
						"user@mail.com",
						"password",
						"username900",
						55,
						Gender.MALE,
						Role.USER
				);
				System.out.println("User token: " + service.register(user).accessToken());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		};
	}

}
