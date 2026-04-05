package com.inventory.service;

import com.inventory.dto.LoginRequestDTO;
import com.inventory.dto.LoginResponseDTO;
import com.inventory.exception.InventoryException;
import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtTokenProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshTokenFromUsername(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new InventoryException("User not found"));

        return new LoginResponseDTO(
            token,
            refreshToken,
            user.getUsername(),
            user.getRole().getName().toString(),
            user.getId()
        );
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new InventoryException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromRefreshToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new InventoryException("User not found"));

        String newAccessToken = tokenProvider.generateTokenFromUsername(username);
        String newRefreshToken = tokenProvider.generateRefreshTokenFromUsername(username);

        return new LoginResponseDTO(
            newAccessToken,
            newRefreshToken,
            user.getUsername(),
            user.getRole().getName().toString(),
            user.getId()
        );
    }

    public void registerUser(String username, String email, String password, Role.RoleType roleType) {
        String normalizedUsername = username == null ? null : username.trim();
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            throw new InventoryException("Username is required");
        }

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new InventoryException("Email is required");
        }

        Role role = roleRepository.findByName(roleType)
            .orElseGet(() -> roleRepository.save(new Role(null, roleType, roleType + " role")));

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            String message = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage().toLowerCase(Locale.ROOT)
                : "";
            if (message.contains("username") || message.contains("ukr43af9ap4edm43mmtq01oddj6")) {
                throw new InventoryException("Username already exists");
            }
            if (message.contains("email") || message.contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new InventoryException("Email already exists");
            }
            throw new InventoryException("Unable to register user right now");
        }
    }
}
