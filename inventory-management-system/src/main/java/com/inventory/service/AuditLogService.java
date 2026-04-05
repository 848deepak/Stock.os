package com.inventory.service;

import com.inventory.model.AuditLog;
import com.inventory.model.User;
import com.inventory.repository.AuditLogRepository;
import com.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for logging audit trails of all entity modifications.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void log(String entityType, Long entityId, String action, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);

        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            userRepository.findByUsername(username).ifPresent(log::setUser);
        }

        auditLogRepository.save(log);
    }
}
