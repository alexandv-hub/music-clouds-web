package com.musicclouds.user.service;

import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.exception.DuplicateResourceException;
import com.musicclouds.exception.RequestValidationException;
import com.musicclouds.exception.ResourceNotFoundException;
import com.musicclouds.user.dao.UserDao;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.dto.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ComponentScan(basePackages = "com.musicclouds.security")
public class UserService {
    private final UserDao userDao;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    private final PasswordEncoder passwordEncoder;

    // Constructor
    public UserService(@Qualifier("jpa")
                       UserDao userDao,
                       FraudClient fraudClient,
                       NotificationClient notificationClient,
                       PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.fraudClient = fraudClient;
        this.notificationClient = notificationClient;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userDao.selectAllUsers();
    }

    public User getUser(Integer id) {
        return userDao.selectUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id [%s] not found".formatted(id)
                ));
    }

    private User createUserFromUserRegistrationRequest(UserRegistrationRequest request) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .age(request.age())
                .gender(request.gender())
                .role(request.role())
                .build();
    }

    public User getUserByEmail(String email) {
        return userDao.selectUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }

    public void registerUser(UserRegistrationRequest request) {

        validateUserRegistrationRequest(request);

        User user = createUserFromUserRegistrationRequest(request);

        userDao.insertUser(user);

        Optional<User> savedUserOptional = userDao.selectUserByEmail(user.getEmail());
        Integer userId = savedUserOptional
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + user.getEmail()))
                .getId();

        isFraudsterUserCheck(userId);

        sendNotification(userId, user);
    }

    private void sendNotification(Integer userId, User user) {
        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        userId,
                        user.getEmail(),
                        String.format("Hi %s, welcome to Music-Clouds...",
                                user.getFirstName())
                )
        );
    }

    private void isFraudsterUserCheck(Integer userId) {
        // todo: check if fraudster
        FraudCheckResponse fraudCheckResponse = null;
        try {
            fraudCheckResponse = fraudClient.isFraudster(userId);
        } catch (Exception e) {
            log.error("Error calling fraud service for userId: " + userId, e);
            // Handle the exception appropriately
        }

        // Handle the response
        if (fraudCheckResponse != null
                && fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
    }

    public void deleteUserById(Integer userId) {
        if (!userDao.existsUserWithId(userId)) {
            throw new ResourceNotFoundException(
                    "user with id [%s] not found".formatted(userId)
            );
        }
        userDao.deleteUserById(userId);
    }

    public Optional<User> updateUser(Integer id, UserUpdateRequest userUpdateRequest) {

        // TODO: for JPA use .getReferenceById(userId) as it does does not bring object into memory and instead a reference
        User user = getUser(id);
        log.info("userId = " + user.getId());

        boolean changes = false;

        if (user.getEmail() != null && !userUpdateRequest.email().equals(user.getEmail())) {
            if (userDao.existsUserWithEmail(userUpdateRequest.email())) {
                log.error("email already taken! ({})...", userUpdateRequest.email());
                throw new DuplicateResourceException(
                        "email already taken!"
                );
            }
            user.setEmail(userUpdateRequest.email());
            changes = true;
        }

        if (user.getFirstName() != null && !userUpdateRequest.firstName().equals(user.getFirstName())) {
            user.setFirstName(userUpdateRequest.firstName());
            changes = true;
        }

        if (user.getLastName() != null && !userUpdateRequest.lastName().equals(user.getLastName())) {
            user.setLastName(userUpdateRequest.lastName());
            changes = true;
        }

        if (user.getUsername() != null && !userUpdateRequest.username().equals(user.getUsername())) {
            user.setUsername(userUpdateRequest.username());
            changes = true;
        }

        if (user.getAge() != null && !userUpdateRequest.age().equals(user.getAge())) {
            user.setAge(userUpdateRequest.age());
            changes = true;
        }

        if (user.getGender() != null && !userUpdateRequest.gender().equals(user.getGender())) {
            user.setGender(userUpdateRequest.gender());
            changes = true;
        }

        if (user.getPassword() != null && !userUpdateRequest.password().equals(user.getPassword())) {
            user.setPassword(userUpdateRequest.password());
            changes = true;
        }

        if (user.getRole() != null && !userUpdateRequest.role().equals(user.getRole())) {
            user.setRole(userUpdateRequest.role());
            changes = true;
        }

        if (!changes) {
            log.error("no data changes found");
            throw new RequestValidationException("no data changes found");
        }

        try {
            return userDao.selectUserById(id).map(usr -> {
                usr.setFirstName(userUpdateRequest.firstName());
                usr.setLastName(userUpdateRequest.lastName());
                usr.setEmail(userUpdateRequest.email());
                usr.setPassword(userUpdateRequest.password());
                usr.setUsername(userUpdateRequest.username());
                usr.setAge(userUpdateRequest.age());
                usr.setGender(userUpdateRequest.gender());
                usr.setRole(userUpdateRequest.role());
                // add other fields here

                return userDao.updateUser(usr);
            });
        } catch (Exception e) {
           log.error(e.getMessage());
           throw e;
        }
    }
    
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public void validateUserRegistrationRequest(UserRegistrationRequest request) {
        // check if email not empty
        if (request.email().isEmpty()) {
            log.error("email field is empty!");
            throw new RequestValidationException(
                    "email field is empty!"
            );
        }

        // check if email valid
        if (!isValidEmail(request.email())) {
            log.error("email is not valid!");
            throw new RequestValidationException(
                    "email is not valid!"
            );
        }

        // check if email not taken
        if (userDao.existsUserWithEmail(request.email())) {
            log.error("email already taken!");

            throw new DuplicateResourceException(
                    "email already taken!"
            );
        }

        // check if username text field not empty
        if (request.username().isEmpty()) {
            log.error("username text field is empty!");

            throw new RequestValidationException(
                    "username text field is empty!"
            );
        }

        // check if username not taken
        if (userDao.existsUserWithUsername(request.username())) {
            log.error("username already taken!");

            throw new DuplicateResourceException(
                    "username already taken!"
            );
        }
    }
}
