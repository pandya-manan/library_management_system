package com.oops.library.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.service.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        passwordResetService.requestPasswordReset(email);
        redirectAttributes.addFlashAttribute("info",
                "If an account exists for that email, we have sent a one-time password.");
        String encodedEmail = UriUtils.encode(email, StandardCharsets.UTF_8);
        return "redirect:/reset-password?email=" + encodedEmail;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "email", required = false) String email,
                                        Model model) {
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", email);
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
                                      @RequestParam("otp") String otp,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }

        try {
            passwordResetService.resetPassword(email, otp, password);
            redirectAttributes.addFlashAttribute("success", "Password reset successful. You may now log in.");
            return "redirect:/login";
        } catch (EnchantedLibraryException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
    }
}

