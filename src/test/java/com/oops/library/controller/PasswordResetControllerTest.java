package com.oops.library.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.service.PasswordResetService;

@WebMvcTest(PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    void forgotPassword_getRendersView() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"));
    }

    @Test
    void forgotPassword_postDelegatesToService() throws Exception {
        mockMvc.perform(post("/forgot-password").param("email", "user@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password?email=user%40example.com"));

        verify(passwordResetService).requestPasswordReset("user@example.com");
    }

    @Test
    void resetPassword_redirectsWhenPasswordsMismatch() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("email", "user@example.com")
                        .param("otp", "123456")
                        .param("password", "secret1")
                        .param("confirmPassword", "secret2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password"));

        verifyNoInteractions(passwordResetService);
    }

    @Test
    void resetPassword_redirectsToLoginOnSuccess() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("email", "user@example.com")
                        .param("otp", "123456")
                        .param("password", "secret")
                        .param("confirmPassword", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(passwordResetService).resetPassword("user@example.com", "123456", "secret");
    }

    @Test
    void resetPassword_redirectsBackWhenServiceFails() throws Exception {
        doThrow(new EnchantedLibraryException("Invalid"))
                .when(passwordResetService)
                .resetPassword("user@example.com", "123456", "secret");

        mockMvc.perform(post("/reset-password")
                        .param("email", "user@example.com")
                        .param("otp", "123456")
                        .param("password", "secret")
                        .param("confirmPassword", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password"));

        verify(passwordResetService).resetPassword("user@example.com", "123456", "secret");
    }
}

