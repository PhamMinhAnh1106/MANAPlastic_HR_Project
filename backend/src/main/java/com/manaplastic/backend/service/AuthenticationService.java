package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AuthenticationRequest;
import com.manaplastic.backend.DTO.AuthenticationResponse;
import com.manaplastic.backend.DTO.RefreshTokenRequest;
import com.manaplastic.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse login(AuthenticationRequest request) {
        // xác thực người dùng (username + password)
        // nếu sai, nó sẽ ném ra exception
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // nếu xác thực thành công thì tìm user
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // tạo JWT token
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // trả về token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    //refesh token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken =request.getRefreshToken();

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token missing");
        }

        final String username = jwtService.extractUsername(refreshToken);

        if(username != null){
            var user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if(jwtService.isTokenValid(refreshToken,user)){
                var accessToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }
}