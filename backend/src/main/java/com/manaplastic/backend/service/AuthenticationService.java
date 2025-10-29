package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AuthenticationRequest;
import com.manaplastic.backend.DTO.AuthenticationResponse;
import com.manaplastic.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        // trả về token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}