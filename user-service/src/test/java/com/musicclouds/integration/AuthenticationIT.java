package com.musicclouds.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.security.auth.AuthenticationRequest;
import com.musicclouds.security.auth.AuthenticationResponse;
import com.musicclouds.security.jwt.JwtService;
import com.musicclouds.user.UserApplication;
import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;
import com.musicclouds.user.dto.UserDTO;
import com.musicclouds.user.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = UserApplication.class, webEnvironment = RANDOM_PORT)
public class AuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private FraudClient fraudClient;

    @MockBean
    private NotificationClient notificationClient;


    private void configureMocks() {
        when(fraudClient.isFraudster(anyInt())).thenReturn(new FraudCheckResponse(false));
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    private static final Random RANDOM = new Random();
    private static final String AUTHENTICATION_PATH = "/api/v1/users/auth";
    private static final String USERS_REGISTER_PATH = "/api/v1/users/auth/register";

    @Test
    void canLogin() {
        // Given
        // create registration userRegistrationRequest
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = firstName + "-" + lastName + "-" + UUID.randomUUID() + "@amigoscode.com";
        String password = "password";
        int age = RANDOM.nextInt(18, 100);
        String username = firstName + "-" + lastName + "-" + age;
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        Role role = Role.ADMIN;

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                firstName, lastName, email, password, username, age, gender, role
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );

        configureMocks();

        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // send a post userRegistrationRequest
        webTestClient.post()
                .uri(USERS_REGISTER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(userRegistrationRequest),
                        UserRegistrationRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        result.getResponseHeaders().forEach((key, value) -> System.out.println(key + ":" + value));

        AuthenticationResponse authenticationResponse = result.getResponseBody();
        String jwtToken = Objects.requireNonNull(authenticationResponse).accessToken();
        UserDTO userDTO = authenticationResponse.userDTO();

        assertThat(jwtService.isTokenValid(
                jwtToken,
                userDTO));

        assertThat(userDTO.firstName()).isEqualTo(firstName);
        assertThat(userDTO.lastName()).isEqualTo(lastName);
        assertThat(userDTO.email()).isEqualTo(email);
        assertThat(userDTO.username()).isEqualTo(username);
        assertThat(userDTO.age()).isEqualTo(age);
        assertThat(userDTO.gender()).isEqualTo(gender);
        assertThat(userDTO.role()).isEqualTo(Role.ADMIN);
    }
}
