package com.musicclouds.user.dao;

import com.musicclouds.user.AbstractTestcontainers;
import com.musicclouds.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class UserJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private UserJDBCDataAccessService underTest;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new UserJDBCDataAccessService(
                getJdbcTemplate(),
                userRowMapper
        );
    }

    @Test
    void selectAllUsers() {
        // Given
        User user = getUserFakeExample();

        underTest.insertUser(user);
        log.info(user.toString());

        // When
        List<User> actual = underTest.selectAllUsers();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @NotNull
    private static User getUserFakeExample() {
        return new User(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.name().username(),
                ThreadLocalRandom.current().nextInt(18, 100),
                FAKER.options().option("Male", "Female")
        );
    }

    @Test
    void selectUserById() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<User> actual = underTest.selectUserById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(c.getLastName()).isEqualTo(user.getLastName());
            assertThat(c.getEmail()).isEqualTo(user.getEmail());
            assertThat(c.getUsername()).isEqualTo(user.getUsername());
        });
    }

    @Test
    void willReturnEmptyWhenSelectUserById() {
        // Given
        int id = 0;

        // When
        var actual = underTest.selectUserById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsUserWithEmail() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        // When
        boolean actual = underTest.existsUserWithEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsUserWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsUserWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsUserWithId() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.existsUserWithId(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsUserWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        int id = -1;

        // When
        var actual = underTest.existsUserWithId(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteUserById() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteUserById(id);

        // Then
        Optional<User> actual = underTest.selectUserById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateUsername() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";

        // When username is changed
        User update = new User();
        update.setId(id);
        update.setUsername(newName);

        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(c.getLastName()).isEqualTo(user.getLastName());
            assertThat(c.getEmail()).isEqualTo(user.getEmail());
            assertThat(c.getUsername()).isEqualTo(newName); // change
        });
    }

    @Test
    void updateUserEmail() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        var newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When email is changed
        User update = new User();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(c.getLastName()).isEqualTo(user.getLastName());
            assertThat(c.getEmail()).isEqualTo(newEmail); // change
            assertThat(c.getUsername()).isEqualTo(user.getUsername());
        });
    }

    @Test
    void willUpdateAllPropertiesUser() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When update with all new fields
        User update = new User();
        update.setId(id);
        update.setFirstName("foo");
        update.setLastName("foo");
        update.setEmail(UUID.randomUUID().toString());
        update.setUsername(UUID.randomUUID().toString());

        int generatedAge;
        do {
            generatedAge = ThreadLocalRandom.current().nextInt(18, 100);
        } while (generatedAge == user.getAge());
        update.setAge(generatedAge);

        update.setGender(user.getGender().equals("Male") ? "Female" : "Male");

        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(id);

        assertThat(actual).isPresent().hasValue(update);
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        User user = getUserFakeExample();
        String email = user.getEmail();

        underTest.insertUser(user);

        int id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(User::getId)
                .findFirst()
                .orElseThrow();

        // When update without no changes
        User update = new User();
        update.setId(id);

        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(c.getLastName()).isEqualTo(user.getLastName());
            assertThat(c.getEmail()).isEqualTo(user.getEmail());
            assertThat(c.getUsername()).isEqualTo(user.getUsername());
        });
    }
}