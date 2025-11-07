package com.manaplastic.backend.controller;

import com.manaplastic.backend.DTO.AuthenticationRequest;
import com.manaplastic.backend.DTO.AuthenticationResponse;
import com.manaplastic.backend.DTO.RefreshTokenRequest;
import com.manaplastic.backend.config.JwtAuthenticationFilter;
import com.manaplastic.backend.service.AuthenticationService;
import com.manaplastic.backend.service.JwtBlacklistService;
import com.manaplastic.backend.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthFilter;

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


  @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String fullAuthHeader = request.getHeader("Authorization");
        String token = null;

        if (fullAuthHeader != null && fullAuthHeader.startsWith("Bearer ")) {
            token = fullAuthHeader.substring(7);
        }

        if (token != null) {
            // 2. Tính toán thời gian hết hạn còn lại
            long expirationSeconds = jwtService.getRemainingTokenExpirationSeconds(token);

            // 3. Đưa Token vào Blacklist
            if (expirationSeconds > 0) {
                jwtBlacklistService.blacklistToken(token, expirationSeconds);
            }
        }

        // 4. Xóa Cookie
        Cookie jwtCookie = new Cookie("access_token", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // Xóa Refresh Token Cookie nếu có
        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logout successful. Token blacklisted.");
    }


}