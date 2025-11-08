package com.oops.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.PasswordResetToken;
import com.oops.library.entity.User;
import com.oops.library.repository.PasswordResetTokenRepository;
import com.oops.library.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Test
    void requestPasswordReset_sendsOtpWhenUserExists() {
        User user = new User() {
            @Override
            public String getType() {
                return "TEST";
            }
        };
        user.setEmail("reader@library.com");

        when(userRepository.findByEmail("reader@library.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)).thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("reader@library.com");

        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getOtp());
        assertEquals(6, savedToken.getOtp().length());
        verify(emailService).sendPasswordResetOtp("reader@library.com", savedToken.getOtp());
    }

    @Test
    void requestPasswordReset_ignoresBlankEmail() {
        passwordResetService.requestPasswordReset(" ");

        verifyNoInteractions(userRepository, tokenRepository, emailService);
    }

    @Test
    void resetPassword_updatesPasswordWhenOtpValid() throws EnchantedLibraryException {
        User user = new User() {
            @Override
            public String getType() {
                return "TEST";
            }
        };
        user.setEmail("reader@library.com");

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setOtp("123456");
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("reader@library.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUserAndOtpAndUsedFalse(user, "123456")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        passwordResetService.resetPassword("reader@library.com", "123456", "new-password");

        assertEquals("encoded-password", user.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
        verify(tokenRepository).deleteByUserAndExpiresAtBefore(eq(user), any(LocalDateTime.class));
    }

    @Test
    void resetPassword_throwsWhenTokenExpired() {
        User user = new User() {
            @Override
            public String getType() {
                return "TEST";
            }
        };
        user.setEmail("reader@library.com");

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setOtp("123456");
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("reader@library.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findByUserAndOtpAndUsedFalse(user, "123456")).thenReturn(Optional.of(token));

        assertThrows(EnchantedLibraryException.class,
                () -> passwordResetService.resetPassword("reader@library.com", "123456", "new-password"));

        verify(userRepository, never()).save(user);
    }

    @Test
    void resetPassword_throwsWhenEmailUnknown() {
        when(userRepository.findByEmail("unknown@library.com")).thenReturn(Optional.empty());

        assertThrows(EnchantedLibraryException.class,
                () -> passwordResetService.resetPassword("unknown@library.com", "123456", "new-password"));
    }
}

