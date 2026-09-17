package com.BankingSystem.Banking_System.config;

import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Check whether Bearer token exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT
        String token = authHeader.substring(7);

        try {

            // 4. Extract email from JWT
            String username = jwtService.extractUsername(token);

            System.out.println("JWT username: " + username);

            // 5. Don't authenticate if authentication already exists
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Find user
                User user = userRepository.findByEmail(username)
                        .orElse(null);

                // 7. Validate token
                if (user != null && jwtService.isTokenValid(token, user)) {

                    // 8. Create Spring Security authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    Collections.emptyList()
                            );

                    // 9. Attach request details
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // 10. Store authentication in SecurityContext
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    System.out.println("Authenticated user: " +
                            authentication.getPrincipal());
                }
            }

        } catch (JwtException | IllegalArgumentException exception) {
            // Invalid/expired/malformed JWT
            // Leave the request unauthenticated.
        }

        // 11. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}