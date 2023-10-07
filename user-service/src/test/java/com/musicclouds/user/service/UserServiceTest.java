package com.musicclouds.user.service;

import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.exception.DuplicateResourceException;
import com.musicclouds.exception.RequestValidationException;
import com.musicclouds.exception.ResourceNotFoundException;
import com.musicclouds.user.dao.UserDao;
import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.dto.UserUpdateRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private FraudClient fraudClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private UserService underTest;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userDao, fraudClient, notificationClient, passwordEncoder);
    }

    @Test
    void getAllUsers() {
        // When
        underTest.getAllUsers();

        // Then
        verify(userDao).selectAllUsers();
    }

    @NotNull
    private static UserRegistrationRequest getUserRegistrationRequestExample() {
        return new UserRegistrationRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password",
                "johndoe",
                35,
                Gender.MALE,
                Role.USER
        );
    }

    @NotNull
    private static User getUserExample() {
        return new User(
                10,
                "Alex",
                "Jason",
                "alex@gmail.com",
                "password",
                "Jason23",
                23,
                Gender.MALE,
                Role.USER
        );
    }

    @Test
    void canGetUser() {
        // Given
        User user = getUserExample();
        int id = user.getId(); // 10

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        // When
        User actual = underTest.getUser(id);

        // Then
        assertThat(actual).isEqualTo(user);
    }

    @Test
    void willThrowWhenGetUserReturnEmptyOptional() {
        // Given
        int id = 10;

        when(userDao.selectUserById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getUser(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user with id [%s] not found".formatted(id));
    }

    @Test
    void canRegisterUser() {
        // Given
        UserRegistrationRequest request = getUserRegistrationRequestExample();

        String passwordHash = "¢5554ml;f;lsd";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password(passwordHash)
                .username("johndoe")
                .age(35)
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        when(userDao.existsUserWithEmail(anyString())).thenReturn(false);
        when(userDao.existsUserWithUsername(anyString())).thenReturn(false);
        when(userDao.selectUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(fraudClient.isFraudster(any())).thenReturn(new FraudCheckResponse(false));

        // When
        underTest.registerUser(request);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).insertUser(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);

        ArgumentCaptor<NotificationRequest> notificationArgumentCaptor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationClient).sendNotification(notificationArgumentCaptor.capture());

        NotificationRequest capturedNotificationRequest = notificationArgumentCaptor.getValue();
        assertThat(capturedNotificationRequest.toUserEmail()).isEqualTo(user.getEmail());
        assertThat(capturedNotificationRequest.toUserId()).isEqualTo(user.getId());
        assertThat(capturedNotificationRequest.message()).contains(user.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenEmailFieldIsEmpty() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Doe",
                "", // empty email
                "password",
                "johndoe",
                30,
                Gender.MALE,
                Role.ADMIN
        );

        // Then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("email field is empty!");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsTaken() {
        // Given
        UserRegistrationRequest request = getUserRegistrationRequestExample();

        when(userDao.existsUserWithEmail(request.email())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email already taken!");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNotValid() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Doe",
                "john@email999", // invalid email
                "password",
                "johndoe",
                35,
                Gender.MALE,
                Role.ADMIN
        );

        // Then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("email is not valid!");
    }

    @Test
    void shouldThrowExceptionWhenUsernameFieldIsEmpty() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password",
                "", // empty username
                35,
                Gender.MALE,
                Role.ADMIN
        );

        // Then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("username text field is empty!");
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsTaken() {
        // Given
        UserRegistrationRequest request = getUserRegistrationRequestExample();

        when(userDao.existsUserWithUsername(request.username())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username already taken!");
    }

    @Test
    void deleteUserById() {
        // Given
        int id = 10;

        when(userDao.existsUserWithId(id)).thenReturn(true);

        // When
        underTest.deleteUserById(id);

        // Then
        verify(userDao).deleteUserById(id);
    }

    @Test
    void willThrowDeleteUserByIdNotExists() {
        // Given
        int id = 10;

        when(userDao.existsUserWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteUserById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("user with id [%s] not found".formatted(id));

        // Then
        verify(userDao, never()).deleteUserById(id);
    }

    @Test
    void canUpdateAllUsersProperties() {
        // Given
        User user = getUserExample();
        int id = user.getId(); // 10

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        String newFirstName = "Alexandro";
        String newLastName = "Jacobson";
        String newEmail = "alexandro@gmail.com";
        String newPassword = "newPassword";
        String newUsername = "Jacobson555";
        Integer newAge = 25;
        Gender newGender = Gender.MALE;
        Role newRole = Role.USER;

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                newFirstName,
                newLastName,
                newEmail,
                newPassword,
                newUsername,
                newAge,
                newGender,
                newRole);

        when(userDao.existsUserWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateUser(id, updateRequest);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).updateUser(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        // Here we are creating an expected User object with the updated properties
        User expectedUser = new User(
                id,
                newFirstName,
                newLastName,
                newEmail,
                newPassword,
                newUsername,
                newAge,
                newGender,
                newRole);

        // And here we are comparing the captured user to the expected user
        assertThat(capturedUser)
                .usingRecursiveComparison()
                .ignoringFields("id") // if id is autogenerated, and you don't want to compare it
                .isEqualTo(expectedUser);
    }

    @Test
    void canUpdateOnlyUserName() {
        // Given
        User user = getUserExample();
        int id = user.getId(); // 10

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        String newFirstName = "Alexandro";
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                newFirstName, // new
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getRole());

        // When
        underTest.updateUser(id, updateRequest);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).updateUser(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        // Create expected user
        User expectedUser = new User(
                id,
                newFirstName, // new
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getRole());

        // Compare capturedUser with expectedUser
        assertThat(capturedUser)
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void willThrowWhenTryingToUpdateUserEmailWhenAlreadyTaken() {
        // Given
        User user = getUserExample();
        int id = user.getId(); // 10

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        String newEmail = "alexandro@gmail.com";
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                user.getFirstName(),
                user.getLastName(),
                newEmail, // new
                user.getPassword(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getRole());

        when(userDao.existsUserWithEmail(newEmail)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.updateUser(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email already taken!");
    }

    @Test
    void willThrowWhenUserUpdateHasNoChanges() {
        // Given
        User user = getUserExample();
        int id = user.getId(); // 10

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getRole());

        // Then
        assertThatThrownBy(() -> underTest.updateUser(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("no data changes found");
    }

}