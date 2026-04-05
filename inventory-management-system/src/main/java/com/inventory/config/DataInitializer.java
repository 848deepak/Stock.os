package com.inventory.config;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@stock.os";
    private static final String ADMIN_PASSWORD = "admin1234@";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = ensureRole(Role.RoleType.ADMIN, "Administrator - Full system access");
        ensureRole(Role.RoleType.MANAGER, "Manager - Inventory management access");
        ensureRole(Role.RoleType.STAFF, "Staff - View-only access");

        User admin = userRepository.findByUsername(ADMIN_USERNAME)
            .orElseGet(User::new);

        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(adminRole);
        admin.setIsActive(true);

        if (admin.getCreatedAt() == null) {
            admin.setCreatedAt(LocalDateTime.now());
        }
        admin.setUpdatedAt(LocalDateTime.now());

        userRepository.save(admin);
    }

    private Role ensureRole(Role.RoleType roleType, String description) {
        return roleRepository.findByName(roleType)
            .orElseGet(() -> roleRepository.save(new Role(null, roleType, description)));
    }
}
