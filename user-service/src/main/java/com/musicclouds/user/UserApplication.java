package com.musicclouds.user;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			var faker = new Faker();
			Random random = new Random();
			Name name = faker.name();
			String firstName = name.firstName();
			String lastName = name.lastName();
			User user = new User(
					firstName,
					lastName,
					firstName.toLowerCase() + "." + lastName.toLowerCase() + "@music-clouds.com",
					random.nextInt(16, 999) + "-test_username"
			);
			userRepository.save(user);
		};
	}

}
