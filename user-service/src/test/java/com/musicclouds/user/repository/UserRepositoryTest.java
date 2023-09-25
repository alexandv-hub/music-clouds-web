package com.musicclouds.user.repository;

import com.musicclouds.user.AbstractTestcontainers;
import com.musicclouds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsUserByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        User user = new User(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                email,
                FAKER.name().username()
        );

        underTest.save(user);

        // When
        var actual = underTest.existsUserByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsUserByEmailFailsWhenEmailNotPresent() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var actual = underTest.existsUserByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsUserById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        User user = new User(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                email,
                FAKER.name().username()
        );

        underTest.save(user);

        int id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.existsUserById(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsUserByIdFailsWhenIdNotPresent() {
        // Given
        int id = -1;

        // When
        var actual = underTest.existsUserById(id);

        // Then
        assertThat(actual).isFalse();
    }
}