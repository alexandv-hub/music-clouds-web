package com.musicclouds.user.dao;

import com.musicclouds.user.domain.User;
import com.musicclouds.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
@Slf4j
public class UserJPADataAccessService implements UserDao {

    private final UserRepository userRepository;

    public UserJPADataAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> selectAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> selectUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> selectUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> selectUserByUsernameOrEmail(String identifier) throws UsernameNotFoundException {
        log.info("Starting selectUserByUsernameOrEmail... identifier:" + identifier);

        // Use the custom query method from UserDao
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(identifier, identifier);

        if (optionalUser.isEmpty()) {
            log.warn("User with identifier " + identifier + " not found");
        }
        return optionalUser.map(user -> user);
    }

    @Override
    public void insertUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsUserWithEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Override
    public boolean existsUserWithId(Integer id) {
        return userRepository.existsUserById(id);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User update) {
        return userRepository.save(update);
    }

    @Override
    public boolean existsUserWithUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

}
