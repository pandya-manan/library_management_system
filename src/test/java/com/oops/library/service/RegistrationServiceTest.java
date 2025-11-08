package com.oops.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oops.library.dto.RegistrationDto;
import com.oops.library.entity.Librarian;
import com.oops.library.entity.Role;
import com.oops.library.entity.User;
import com.oops.library.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistrationService registrationService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void registerUser_encodesPasswordAndSavesUser() {
        RegistrationDto dto = new RegistrationDto();
        dto.setName("Hermione Granger");
        dto.setEmail("hermione@hogwarts.edu");
        dto.setPassword("leviosa");
        dto.setRole("LIBRARIAN");
        dto.setProfileImagePath("/uploads/profile/hermione.png");

        when(passwordEncoder.encode("leviosa")).thenReturn("encoded-pass");
        when(userRepository.saveAndFlush(userCaptor.capture())).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        registrationService.registerUser(dto);

        verify(passwordEncoder).encode("leviosa");
        verify(emailService).sendSignupConfirmation("hermione@hogwarts.edu", "Hermione Granger");

        User savedUser = userCaptor.getValue();
        assertTrue(savedUser instanceof Librarian);
        assertEquals("encoded-pass", savedUser.getPassword());
        assertEquals(Role.LIBRARIAN, savedUser.getRole());
        assertEquals("/uploads/profile/hermione.png", savedUser.getProfileImagePath());
    }
}

