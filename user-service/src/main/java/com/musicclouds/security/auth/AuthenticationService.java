package com.musicclouds.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicclouds.security.jwt.JwtService;
import com.musicclouds.security.token.Token;
import com.musicclouds.security.token.TokenRepository;
import com.musicclouds.security.token.TokenType;
import com.musicclouds.user.domain.User;
import com.musicclouds.user.dto.UserDTO;
import com.musicclouds.user.dto.UserDTOMapper;
import com.musicclouds.user.dto.UserRegistrationRequest;
import com.musicclouds.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDTOMapper userDTOMapper;

    private final JwtService jwtService;
    private final UserService userService;

    private final TokenRepository tokenRepository;

    public AuthenticationResponse register(UserRegistrationRequest request) {
        userService.registerUser(request); // Register user

        User savedUser = userService.getUserByEmail(request.email());

        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);

        UserDTO userDTO = userDTOMapper.apply(savedUser);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userDTO(userDTO)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userService.getUserByEmail(request.email());

        var jwtToken = jwtService.generateToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        UserDTO userDTO = userDTOMapper.apply(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userDTO(userDTO)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userService.getUserByEmail(userEmail);

            UserDTO userDTO = userDTOMapper.apply(user);

            if (jwtService.isTokenValid(refreshToken, userDTO)) {
                var accessToken = jwtService.generateToken(user);

                revokeAllUserTokens(user);

                saveUserToken(user, accessToken);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userDTO(userDTO)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
