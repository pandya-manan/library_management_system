package com.oops.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.Guest;
import com.oops.library.entity.User;
import com.oops.library.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserInformationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInformationService userInformationService;

    @Test
    void getAllRegisteredUsers_returnsListWhenPresent() throws EnchantedLibraryException {
        User user = new Guest();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userInformationService.getAllRegisteredUsers();

        assertEquals(1, result.size());
        assertSame(user, result.get(0));
    }

    @Test
    void getAllRegisteredUsers_throwsWhenEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EnchantedLibraryException.class, () -> userInformationService.getAllRegisteredUsers());
    }

    @Test
    void findByEmail_returnsUserWhenFound() throws EnchantedLibraryException {
        User user = new Guest();
        when(userRepository.findByEmail("guest@domain.com")).thenReturn(Optional.of(user));

        User result = userInformationService.findByEmail("guest@domain.com");

        assertSame(user, result);
    }

    @Test
    void findByEmail_throwsWhenMissing() {
        when(userRepository.findByEmail("missing@domain.com")).thenReturn(Optional.empty());

        assertThrows(EnchantedLibraryException.class, () -> userInformationService.findByEmail("missing@domain.com"));
    }
}


