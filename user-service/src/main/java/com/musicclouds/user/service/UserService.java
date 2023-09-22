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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@EnableFeignClients(basePackages = "com.musicclouds.clients")
public class UserService {
    private final UserDao userDao;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    // Constructor
    public UserService(@Qualifier("jdbc") UserDao userDao,
                                          FraudClient fraudClient,
                                          NotificationClient notificationClient) {
        this.userDao = userDao;
        this.fraudClient = fraudClient;
        this.notificationClient = notificationClient;
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

    public void registerUser(UserRegistrationRequest request) {
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
        if (userDao.existsPersonWithEmail(request.email())) {
            log.error("email already taken!");

            throw new DuplicateResourceException(
                    "email already taken!"
            );
        }

        // check if username not taken
        if (userDao.existsPersonWithUsername(request.username())) {
            log.error("username already taken!");

            throw new DuplicateResourceException(
                    "username already taken!"
            );
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .username(request.username())
                .build();

        userDao.insertUser(user);

        // todo: check if fraudster
        Optional<User> savedUserOptional = userDao.selectUserByEmail(user.getEmail());
        Integer userId = savedUserOptional
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + user.getEmail()))
                .getId();
        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(userId);

        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

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

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public void deleteUserById(Integer id) {
        userDao.deleteUserById(id);
    }

    public Optional<User> updateUser(Integer id, UserUpdateRequest userUpdateRequest) {

        // TODO: for JPA use .getReferenceById(userId) as it does does not bring object into memory and instead a reference
        User user = getUser(id);

        boolean changes = false;

        if (user.getEmail() != null && !userUpdateRequest.email().equals(user.getEmail())) {
            if (userDao.existsPersonWithEmail(userUpdateRequest.email())) {
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

        if (!changes) {
            log.error("no data changes found");
            throw new RequestValidationException("no data changes found");
        }

        return userDao.selectUserById(id).map(usr -> {
            usr.setFirstName(userUpdateRequest.firstName());
            usr.setLastName(userUpdateRequest.lastName());
            usr.setEmail(userUpdateRequest.email());
            usr.setUsername(userUpdateRequest.username());
            // add other fields here

            return userDao.updateUser(usr);
        });
    }

}
