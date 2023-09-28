package com.musicclouds.user.dto;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String email,
        String username,
        Integer age,
        String gender
) {
}
