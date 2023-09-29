package com.musicclouds.user.dto;

import com.musicclouds.user.domain.Gender;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String email,
        String username,
        Integer age,
        Gender gender
) {
}
