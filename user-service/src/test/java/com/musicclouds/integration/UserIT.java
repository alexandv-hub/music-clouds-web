package com.musicclouds.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.user.UserApplication;
import com.musicclouds.user.domain.User;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"server.port=8080"},
        classes = UserApplication.class)
@AutoConfigureWebTestClient
@Testcontainers
@Slf4j
public class UserIT {

    @Autowired
    private WebTestClient webTestClient;

    private static final String CUSTOMER_URI = "/api/v1/users";

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
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String username = fakerName.username();

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, username
        );

        configureMocks();

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI + "/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all users
        List<User> allUsers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<User>() {
                })
                .returnResult()
                .getResponseBody();

        int id = Objects.requireNonNull(allUsers).stream()
                .filter(user -> user.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // delete user
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // get user by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateUser() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String username = fakerName.username();

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, username
        );

        configureMocks();

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI + "/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all users
        List<User> allUsers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<User>() {
                })
                .returnResult()
                .getResponseBody();


        int id = Objects.requireNonNull(allUsers).stream()
                .filter(user -> user.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // update user
        String newFirstName = "Ali";

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                newFirstName, lastName, email, username
        );

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), UserUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get user by id
        User updatedUser = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        User expected = new User(
                newFirstName, lastName, email, username
        );

        assertThat(updatedUser).isEqualTo(expected);
    }

    @Test
    void canRegisterUser() throws IOException {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
        String username = fakerName.username();

        UserRegistrationRequest request = new UserRegistrationRequest(
                firstName, lastName, email, username
        );

        configureMocks();

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI + "/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all users
        List<User> allUsers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<User>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that user is present
        User expectedUser = new User(
                firstName, lastName, email, username
        );

        assertThat(allUsers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedUser);

        int id = Objects.requireNonNull(allUsers).stream()
                .filter(user -> user.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        expectedUser.setId(id);

        // get user by id
        String responseBody = webTestClient.get()
                .uri(CUSTOMER_URI + "/id/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        ObjectMapper objectMapper = new ObjectMapper();
        User actual = objectMapper.readValue(responseBody, User.class);

        assertThat(actual).isEqualTo(expectedUser);
    }

}
