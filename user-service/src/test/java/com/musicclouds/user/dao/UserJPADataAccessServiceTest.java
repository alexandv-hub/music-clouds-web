package com.musicclouds.user.dao;

import com.musicclouds.user.AbstractTestcontainers;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class UserJPADataAccessServiceTest extends AbstractTestcontainers {

    private UserJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UserJPADataAccessService(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllUsers() {
        // When
        underTest.selectAllUsers();

        // Then
        verify(userRepository).findAll();
    }

    @Test
    void selectUserById() {
        // Given
        int id = 1;

        // When
        underTest.selectUserById(id);

        // Then
        verify(userRepository).findById(id);
    }

    @Test
    void insertUser() {
        // Given
        User user = new User(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.name().username()
        );

        // When
        underTest.insertUser(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    void existsUserWithEmail() {
        // Given
        String email = "foo@gmail.com";

        // When
        underTest.existsUserWithEmail(email);

        // Then
        verify(userRepository).existsUserByEmail(email);
    }

    @Test
    void existsUserWithId() {
        // Given
        int id = 1;

        // When
        underTest.existsUserWithId(id);

        // Then
        verify(userRepository).existsUserById(id);
    }

    @Test
    void deleteUserById() {
        // Given
        int id = 1;

        // When
        underTest.deleteUserById(id);

        // Then
        verify(userRepository).deleteById(id);
    }

    @Test
    void updateUser() {
        // Given
        User user = new User(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.name().username()
        );

        // When
        underTest.updateUser(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    void existsUserWithUsername() {
        // Given
        String username = "Jason19";

        // When
        underTest.existsUserWithUsername(username);

        // Then
        verify(userRepository).existsUserByUsername(username);
    }

}