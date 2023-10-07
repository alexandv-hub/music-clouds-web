package com.musicclouds.user.dao;

import com.musicclouds.user.AbstractTestcontainers;
import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;
import com.musicclouds.user.domain.User;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

class UserRowMapperTest extends AbstractTestcontainers {

    @Test
    void mapRow() throws SQLException {
        // Given
        UserRowMapper userRowMapper = new UserRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("first_name")).thenReturn("Jamila");
        when(resultSet.getString("last_name")).thenReturn("Jason");
        when(resultSet.getString("email")).thenReturn("jamila@gmail.com");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("username")).thenReturn("Jamila19");
        when(resultSet.getInt("age")).thenReturn(19);
        when(resultSet.getString("gender")).thenReturn(Gender.FEMALE.toString());
        when(resultSet.getString("role")).thenReturn(Role.ADMIN.name());

        // When
        User actual = userRowMapper.mapRow(resultSet, 1);

        // Then
        User expected = new User(
                1,
                "Jamila",
                "Jason",
                "jamila@gmail.com",
                "password",
                "Jamila19",
                19,
                Gender.FEMALE,
                Role.ADMIN
        );
        assertThat(actual).isEqualTo(expected);
    }
}