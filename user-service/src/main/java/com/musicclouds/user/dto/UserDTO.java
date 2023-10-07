package com.musicclouds.user.dto;

import com.musicclouds.user.domain.Gender;
import com.musicclouds.user.domain.Role;

public record UserDTO(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String username,
        Integer age,
        Gender gender,
        Role role
){
}
