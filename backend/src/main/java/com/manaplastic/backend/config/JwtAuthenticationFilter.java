package com.manaplastic.backend.config;

import com.manaplastic.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        // Skip cho OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip cho public endpoints
        if (requestURI.equals("/login") || requestURI.equals("/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip cho static resources (JS, CSS, images, fonts)
        if (requestURI.matches(".*\\.(js|css|ico|png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot|map)$") ||
                requestURI.startsWith("/assets/") ||
                requestURI.startsWith("/browser/") ||
                requestURI.equals("/") ||
                requestURI.equals("/index.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip cho Swagger
        if (requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestURI.equals("/login") || requestURI.equals("/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // bỏ qua và cho đi tiếp
            return;
        }

        // lấy JWT Token từ Header
        final String jwt = authHeader.substring(7); // bỏ "Bearer "
        if (jwt.isEmpty()) {
            System.err.println("Lỗi JWT không hợp lệ: Token rỗng sau khi cắt 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }
        // lấy username từ token
//        final String username = jwtService.extractUsername(jwt);
        final String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (io.jsonwebtoken.JwtException e) {
            System.err.println("Lỗi JWT không hợp lệ: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // bước kiểm tra (Nếu user chưa được xác thực)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}