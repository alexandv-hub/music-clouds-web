package com.musicclouds.user.service;

import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.clients.fraud.FraudClient;
import com.musicclouds.clients.notification.NotificationClient;
import com.musicclouds.clients.notification.NotificationRequest;
import com.musicclouds.exception.DuplicateResourceException;
import com.musicclouds.exception.RequestValidationException;
import com.musicclouds.exception.ResourceNotFoundException;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.dto.UserUpdateRequest;
import com.musicclouds.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@EnableFeignClients(basePackages = "com.musicclouds.clients")
public class UserService {
    private final UserRepository userRepository;
    private final NotificationClient notificationClient;
    private final FraudClient fraudClient;

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
        if (userRepository.existsUserByEmail(request.email())) {
            log.error("email already taken!");

            throw new DuplicateResourceException(
                    "email already taken!"
            );
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .username(request.username())
                .build();

        userRepository.saveAndFlush(user);

        // todo: check if fraudster
        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(user.getId());

        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        user.getId(),
                        user.getEmail(),
                        String.format("Hi %s, welcome to Music-Clouds...",
                                user.getFirstName())
                )
        );
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id [%s] not found".formatted(id)
                ));
    }

    public Optional<User> updateUser(Integer id, User updatedUser) {

        // TODO: for JPA use .getReferenceById(userId) as it does does not bring object into memory and instead a reference
        User user = getUser(id);

        boolean changes = false;

        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsUserByEmail(updatedUser.getEmail())) {
                log.error("email already taken! ({})...", updatedUser.getEmail());
                throw new DuplicateResourceException(
                        "email already taken!"
                );
            }
            user.setEmail(updatedUser.getEmail());
            changes = true;
        }

        if (user.getFirstName() != null && !updatedUser.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updatedUser.getFirstName());
            changes = true;
        }

        if (user.getLastName() != null && !updatedUser.getLastName().equals(user.getLastName())) {
            user.setLastName(updatedUser.getLastName());
            changes = true;
        }

        if (user.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            user.setUsername(updatedUser.getUsername());
            changes = true;
        }

        if (!changes) {
            log.error("no data changes found");
            throw new RequestValidationException("no data changes found");
        }

        return userRepository.findById(id).map(usr -> {
            usr.setFirstName(updatedUser.getFirstName());
            usr.setLastName(updatedUser.getLastName());
            usr.setEmail(updatedUser.getEmail());
            usr.setUsername(updatedUser.getUsername());
            // add other fields here
            return userRepository.save(usr);
        });
    }

    public void updateUser(Integer userId,
                               UserUpdateRequest updateRequest) {
        // TODO: for JPA use .getReferenceById(userId) as it does does not bring object into memory and instead a reference
        User user = getUser(userId);

        boolean changes = false;

        if (updateRequest.email() != null && !updateRequest.email().equals(user.getEmail())) {
            if (userRepository.existsUserByEmail(updateRequest.email())) {
                log.error("email already taken");
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            user.setEmail(updateRequest.email());
            changes = true;
        }

        if (updateRequest.firstName() != null && !updateRequest.firstName().equals(user.getFirstName())) {
            user.setFirstName(updateRequest.firstName());
            changes = true;
        }

        if (updateRequest.lastName() != null && !updateRequest.lastName().equals(user.getLastName())) {
            user.setLastName(updateRequest.firstName());
            changes = true;
        }

        if (updateRequest.username() != null && !updateRequest.username().equals(user.getUsername())) {
            user.setUsername(updateRequest.username());
            changes = true;
        }

        if (!changes) {
            log.error("no data changes found");
            throw new RequestValidationException("no data changes found");
        }
        userRepository.save(user);
    }

    public void addUser(UserRegistrationRequest userRegistrationRequest) {
        // check if email exists
        String email = userRegistrationRequest.email();
        if (userRepository.existsUserByEmail(email)) {
            log.error("email already taken!");
            throw new DuplicateResourceException(
                    "email already taken!"
            );
        }

        // add
        User user = new User(
                userRegistrationRequest.firstName(),
                userRegistrationRequest.lastName(),
                userRegistrationRequest.email(),
                userRegistrationRequest.username()
        );

        userRepository.save(user);
    }
}
