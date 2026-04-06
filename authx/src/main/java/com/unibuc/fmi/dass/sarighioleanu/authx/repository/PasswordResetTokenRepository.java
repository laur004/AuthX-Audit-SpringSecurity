package com.unibuc.fmi.dass.sarighioleanu.authx.repository;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
