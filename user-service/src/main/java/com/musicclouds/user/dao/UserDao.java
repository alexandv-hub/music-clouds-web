package com.musicclouds.user.dao;

import com.musicclouds.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> selectAllUsers();
    Optional<User> selectUserById(Integer id);
    void insertUser(User user);
    boolean existsPersonWithEmail(String email);
    boolean existsPersonWithId(Integer id);
    void deleteUserById(Integer userId);
    User updateUser(User update);
}
