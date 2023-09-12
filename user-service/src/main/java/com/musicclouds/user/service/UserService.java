package com.musicclouds.user.service;

import com.musicclouds.user.domain.User;

import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(UserRegistrationRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .username(request.username())
                .build();
        // todo: check if email valid
        // todo: check if email not taken
        userRepository.saveAndFlush(user);

    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public User getUser(Long id) {
        return userRepository.getReferenceById(Math.toIntExact(id));
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(Math.toIntExact(id)).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setUsername(updatedUser.getUsername());
            // add other fields here
            return userRepository.save(user);
        });
    }
}
