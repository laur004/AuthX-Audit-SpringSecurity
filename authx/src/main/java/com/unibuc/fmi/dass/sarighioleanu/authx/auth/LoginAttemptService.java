package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.AuditLogService;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public LoginAttemptService(
            UserRepository userRepository,
            AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public void loginSucceeded(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        });
    }

    public void loginFailed(String email, String ipAddress) {
        userRepository.findByEmail(email).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                auditLogService.logAccountBlocked(user.getId().toString(), ipAddress);
            }

            userRepository.save(user);
        });
    }
}