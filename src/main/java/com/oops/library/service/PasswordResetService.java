package com.oops.library.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.PasswordResetToken;
import com.oops.library.entity.User;
import com.oops.library.repository.PasswordResetTokenRepository;
import com.oops.library.repository.UserRepository;

@Service
public class PasswordResetService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_TTL_MINUTES = 10;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Silently return to avoid user enumeration
            return;
        }

        User user = userOpt.get();

        tokenRepository.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .ifPresent(existing -> {
                    existing.setUsed(true);
                    tokenRepository.save(existing);
                });

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setOtp(generateOtp());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES));

        tokenRepository.save(token);

        emailService.sendPasswordResetOtp(user.getEmail(), token.getOtp());
    }

    public void resetPassword(String email, String otp, String newPassword) throws EnchantedLibraryException {
        if (email == null || otp == null || newPassword == null
                || email.isBlank() || otp.isBlank() || newPassword.isBlank()) {
            throw new EnchantedLibraryException("Invalid reset request");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EnchantedLibraryException("Invalid email or OTP"));

        PasswordResetToken token = tokenRepository.findByUserAndOtpAndUsedFalse(user, otp)
                .orElseThrow(() -> new EnchantedLibraryException("Invalid email or OTP"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EnchantedLibraryException("OTP has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        tokenRepository.deleteByUserAndExpiresAtBefore(user, LocalDateTime.now().minusHours(1));
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int number = random.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", number);
    }
}

