package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.AuthenticationRequest;
import com.manaplastic.backend.DTO.AuthenticationResponse;
import com.manaplastic.backend.DTO.RefreshTokenRequest;
import com.manaplastic.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken( @RequestBody RefreshTokenRequest request ) {
        try{
            return ResponseEntity.ok(authService.refreshToken(request));
        }catch (Exception e){
            return ResponseEntity.status(401).body(null);
        }
    }


}