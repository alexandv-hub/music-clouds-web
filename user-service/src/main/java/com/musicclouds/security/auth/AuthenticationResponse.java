package com.musicclouds.security.auth;

import com.musicclouds.user.dto.UserDTO;
import lombok.Builder;

@Builder
public record AuthenticationResponse (
        String accessToken,
        String refreshToken,
        UserDTO userDTO) {
}
