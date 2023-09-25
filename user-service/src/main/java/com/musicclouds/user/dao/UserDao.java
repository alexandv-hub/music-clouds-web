package com.musicclouds.user.dao;

import com.musicclouds.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> selectAllUsers();
    Optional<User> selectUserById(Integer id);
    Optional<User> selectUserByEmail(String email);
    void insertUser(User user);
    boolean existsUserWithEmail(String email);
    boolean existsUserWithId(Integer id);
    void deleteUserById(Integer userId);
    User updateUser(User update);
    boolean existsUserWithUsername(String username);
}
