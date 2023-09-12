package com.musicclouds.user.dto;

public record UserRegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String username) {
}
