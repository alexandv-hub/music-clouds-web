package com.musicclouds.security.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
