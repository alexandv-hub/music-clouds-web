package com.musicclouds.user.dao;

import com.musicclouds.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class UserJDBCDataAccessService implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                     UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public List<User> selectAllUsers() {
        var sql = """
                SELECT id, first_name, last_name, email, username, age, gender
                FROM _user
                """;

        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> selectUserById(Integer id) {
        var sql = """
                SELECT id, first_name, last_name, email, username, age, gender
                FROM _user
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> selectUserByEmail(String email) {
        var sql = """
                SELECT id, first_name, last_name, email, username, age, gender
                FROM _user
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, email)
                .stream()
                .findFirst();
    }

    @Override
    public void insertUser(User _user) {
        var sql = """
                INSERT INTO _user(first_name, last_name, email, username, age, gender)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        int result = jdbcTemplate.update(
                sql,
                _user.getFirstName(),
                _user.getLastName(),
                _user.getEmail(),
                _user.getUsername(),
                _user.getAge(),
                _user.getGender().toString()
                );

        System.out.println("insertUser result " + result);
    }

    @Override
    public boolean existsUserWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM _user
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsUserWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM _user
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteUserById(Integer userId) {
        var sql = """
                DELETE
                FROM _user
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, userId);
        System.out.println("deleteUserById result = " + result);
    }

    @Override
    public User updateUser(User update) {
        if (update.getFirstName() != null) {
            String sql = "UPDATE _user SET first_name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getFirstName(),
                    update.getId()
            );
            System.out.println("update _user first_name result = " + result);
        }
        if (update.getLastName() != null) {
            String sql = "UPDATE _user SET last_name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getLastName(),
                    update.getId()
            );
            System.out.println("update _user last_name result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE _user SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId());
            System.out.println("update _user email result = " + result);
        }

        if (update.getUsername() != null) {
            String sql = "UPDATE _user SET username = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getUsername(),
                    update.getId()
            );
            System.out.println("update _user username result = " + result);
        }

        if (update.getAge() != null) {
            String sql = "UPDATE _user SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
            System.out.println("update _user age result = " + result);
        }

        if (update.getGender() != null) {
            String sql = "UPDATE _user SET gender = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getGender().toString(),
                    update.getId()
            );
            System.out.println("update _user gender result = " + result);
        }
        return selectUserById(update.getId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found after update for ID: " + update.getId()));
    }

    @Override
    public boolean existsUserWithUsername(String username) {
        var sql = """
                SELECT count(id)
                FROM _user
                WHERE username = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}
