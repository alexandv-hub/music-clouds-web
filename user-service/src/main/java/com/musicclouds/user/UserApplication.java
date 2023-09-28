package com.musicclouds.user;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.musicclouds.clients")
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			var faker = new Faker();
			Name fakerName = faker.name();
			String firstName = fakerName.firstName();
			String lastName = fakerName.lastName();
			String username = fakerName.username();
			Integer age = ThreadLocalRandom.current().nextInt(18, 100);
			String gender = faker.options().option("Male", "Female");

			User user = new User(
					firstName,
					lastName,
					firstName.toLowerCase() + "." + lastName.toLowerCase() + "@music-clouds.com",
					username,
					age,
					gender
			);
			userRepository.save(user);
		};
	}

}
