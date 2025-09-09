package com.unito.randomfilm.security;

import com.unito.randomfilm.dto.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${tokenpassword}")
    private String tokenPassword;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(tokenPassword.getBytes());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("JWT Filter - Processing request: {} {}", method, uri);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("Permessa richiesta OPTIONS per CORS preflight");
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromHeader(request);
        log.info("Token estratto: {}", token != null ? "presente" : "assente");

        if (token == null) {
            log.warn("Token assente per richiesta: {} {}", method, uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token di autenticazione richiesto\"}");
            return;
        }

        log.info("Token found, validating...");
        if (!validateToken(token)) {
            log.warn("Invalid token for request: {} {}", method, uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token di autenticazione non valido\"}");
            return;
        }

        try {
            log.info("Inizio estrazione claims dal token");
            Claims claims = getClaimsFromToken(token);
            log.info("Claims estratti con successo");

            // DEBUG
            log.info("Claims completi: {}", claims);
            log.info("Subject: {}", claims.getSubject());
            log.info("Username claim: {}", claims.get("username"));
            log.info("Email claim: {}", claims.get("email"));
            log.info("Name claim: {}", claims.get("name"));

            // Il subject è l'email
            String email = claims.getSubject();
            log.info("Email estratta dal token (subject): {}", email);


            Long userId = null;
            try {
                userId = claims.get("userId", Long.class);
                log.info("UserId estratto dai claims: {}", userId);
            } catch (Exception e) {
                log.warn("UserId non trovato nei claims: {}", e.getMessage());
            }

            // Crea oggetto UserInfo con i dati dal JWT
            UserInfo userInfo = new UserInfo(
                    userId,
                    claims.get("username", String.class),
                    email, // Usa l'email dal subject
                    claims.get("name", String.class)
            );

            log.info("User authenticated: {}", userInfo.getUsername());

            // Salva nel context della richiesta
            request.setAttribute("userInfo", userInfo);

        } catch (Exception e) {
            log.error("Error extracting user info from token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Errore nell'elaborazione del token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims getClaimsFromToken(String token) {
        log.info("Estraendo claims dal token...");
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.info("Claims estratti: {}", claims);
        return claims;
    }

    private boolean validateToken(String token) {
        try {
            log.info("Validando token...");
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            log.info("Token validato con successo");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token non valido: {}", e.getMessage());
            return false;
        }
    }
}