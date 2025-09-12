package com.gangchu.gangchutrip.global.security;


import com.gangchu.gangchutrip.global.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
        try {
            String token = resolve(req);
            if (token != null && tokenProvider.validate(token)) {
                var auth = tokenProvider.getAuthentication(token);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            }
        } catch (SecurityException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
            return;
        }
        chain.doFilter(req, res);
    }

    private String resolve(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        return (h != null && h.startsWith(BEARER)) ? h.substring(BEARER.length()) : null;
    }

//    private void setupAuthentication(HttpServletRequest request, String token) {
//        String username = jwtUtil.(token);
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(username, null, null);
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
//            return authHeader.substring(BEARER_PREFIX.length());
//        }
//        return null;
//    }
}
