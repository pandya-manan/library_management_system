package com.oops.library.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oops.library.entity.PasswordResetToken;
import com.oops.library.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    Optional<PasswordResetToken> findByUserAndOtpAndUsedFalse(User user, String otp);

    void deleteByUserAndExpiresAtBefore(User user, LocalDateTime cutoff);
}


