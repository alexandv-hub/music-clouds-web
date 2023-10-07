package com.musicclouds.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.security.auth.AuthenticationRequest;
import com.musicclouds.security.auth.AuthenticationResponse;
import com.musicclouds.user.UserApplication;
import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;
import com.musicclouds.user.dto.UserDTO;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.dto.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"server.port=8080"},
        classes = UserApplication.class)
@AutoConfigureWebTestClient
@Testcontainers
@Slf4j
public class UserIT {

    @Autowired
    private WebTestClient webTestClient;

    private static final String USERS_URI = "/api/v1/users";

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"));

    @MockBean
    private FraudClient fraudClient;

    @MockBean
    private NotificationClient notificationClient;

    private void configureMocks() {
        when(fraudClient.isFraudster(anyInt())).thenReturn(new FraudCheckResponse(false));
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void canDeleteUser() {
        // create userRegistrationRequest request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String password = "password";
        String username = fakerName.username();
        Integer age = ThreadLocalRandom.current().nextInt(18, 100);
        Gender gender = ThreadLocalRandom.current().nextInt(100) % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        Role role = Role.ADMIN;

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, password , username, age, gender, role
        );

        UserRegistrationRequest request2 = new UserRegistrationRequest(
                firstName, lastName, email + ".uk", password ,username + "-uk", age, gender, role
        );

        configureMocks();

        // Send a post AuthenticationRequest and get isUnauthorized
        webTestClient.post()
                .uri(USERS_URI + "/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // send a post userRegistrationRequest to create user1 and get isOk
        webTestClient.post()
                .uri(USERS_URI + "/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // send a post userRegistrationRequest to create user2 and get isOk
        webTestClient.post()
                .uri(USERS_URI + "/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send a post AuthenticationRequest with user2 credentials and get isOk and save jwtToken
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(USERS_URI + "/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(request2), AuthenticationRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody())
                .accessToken();

        // get all users being authenticated like user2
        List<UserDTO> allUsers = webTestClient.get()
                .uri(USERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<UserDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // get user1 id
        int id = Objects.requireNonNull(allUsers).stream()
                .filter(user -> user.email().equals(email))
                .map(UserDTO::id)
                .findFirst()
                .orElseThrow();

        // user 2 deletes user 1
        webTestClient.delete()
                .uri(USERS_URI + "/{id}", id)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // user2 gets user1 by id and gets back isNotFound
        webTestClient.get()
                .uri(USERS_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateUser() {
        // create userRegistrationRequest request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String password = "password";
        String username = fakerName.username();
        Integer age = ThreadLocalRandom.current().nextInt(18, 100);
        Gender gender = ThreadLocalRandom.current().nextInt(100) % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        Role role = Role.ADMIN;

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, password, username, age, gender, role
        );

        configureMocks();

        // Send a post AuthenticationRequest and get isUnauthorized
        webTestClient.post()
                .uri(USERS_URI + "/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // Send a post userRegistrationRequest and get isOk
        webTestClient.post()
                .uri(USERS_URI + "/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send a post AuthenticationRequest and get isOk
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(USERS_URI + "/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(request), AuthenticationRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody())
                .accessToken();

        // Get all users
        List<UserDTO> allUsers = webTestClient.get()
                .uri(USERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(UserDTO.class)
                .returnResult()
                .getResponseBody();

        int id = Objects.requireNonNull(allUsers).stream()
                .filter(user -> user.email().equals(email))
                .map(UserDTO::id)
                .findFirst()
                .orElseThrow();

        // update user
        String newFirstName = "Ali";

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                newFirstName, lastName, email,  password, username, age, gender, role
        );

        // Send a put UserUpdateRequest and get isOk
        webTestClient.put()
                .uri(USERS_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), UserUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get updated user by id
        UserDTO updatedUserById = webTestClient.get()
                .uri(USERS_URI + "/id/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDTO.class)
                .returnResult()
                .getResponseBody();

        // Construct the expected user
        UserDTO expectedUser = new UserDTO(
                id,
                newFirstName,
                lastName,
                email,
                username,
                age,
                gender,
                role
        );

        assertThat(updatedUserById).isEqualTo(expectedUser);
    }

    @Test
    void canRegisterUser() {
        // create userRegistrationRequest request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String password = "password";
        String username = fakerName.username();
        Integer age = ThreadLocalRandom.current().nextInt(18, 100);
        Gender gender = ThreadLocalRandom.current().nextInt(100) % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        Role role = Role.ADMIN;

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, password, username, age, gender, role
        );

        configureMocks();

        // Send a post AuthenticationRequest and get isUnauthorized
        webTestClient.post()
                .uri(USERS_URI + "/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // Send a post userRegistrationRequest and get isOk
        webTestClient.post()
                .uri(USERS_URI + "/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send a post AuthenticationRequest and get isOk
        String jwtToken = Objects.requireNonNull(webTestClient.post()
                        .uri(USERS_URI + "/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(request), AuthenticationRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody())
                .accessToken();

        // Get all users
        List<UserDTO> allUsers = webTestClient.get()
                .uri(USERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(UserDTO.class)
                .returnResult()
                .getResponseBody();

        // Extract the user from the list
        assert allUsers != null;
        UserDTO foundUser = allUsers.stream()
                .filter(user -> user.email().equals(email))
                .findFirst()
                .orElseThrow();

        // Construct the expected user
        UserDTO expectedUser = new UserDTO(
                foundUser.id(),
                firstName,
                lastName,
                email,
                username,
                age,
                gender,
                role
        );

        assertThat(foundUser).isEqualTo(expectedUser);

        // Get user by id
        UserDTO userById = webTestClient.get()
                .uri(USERS_URI + "/id/{id}", foundUser.id())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(userById).isEqualTo(expectedUser);
    }

}
