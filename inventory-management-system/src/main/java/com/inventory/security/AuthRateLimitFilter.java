package com.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Deque<Long>> requestWindows = new ConcurrentHashMap<>();

    @Value("${app.auth-rate-limit.max-requests:20}")
    private int maxRequests;

    @Value("${app.auth-rate-limit.window-seconds:60}")
    private long windowSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!isRateLimitedAuthPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = request.getRemoteAddr() + ":" + path;
        long now = Instant.now().getEpochSecond();
        Deque<Long> timestamps = requestWindows.computeIfAbsent(clientKey, key -> new ArrayDeque<>());

        synchronized (timestamps) {
            long earliestAllowed = now - windowSeconds;
            while (!timestamps.isEmpty() && timestamps.peekFirst() < earliestAllowed) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Too many authentication attempts. Please try again later.\"}");
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedAuthPath(String path) {
        return path.endsWith("/auth/login") || path.endsWith("/auth/refresh") || path.endsWith("/auth/register");
    }
}
