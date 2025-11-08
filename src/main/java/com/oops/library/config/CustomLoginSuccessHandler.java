package com.oops.library.config;

import java.io.IOException;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.oops.library.entity.User;
import com.oops.library.repository.UserRepository;
import com.oops.library.service.EmailService;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final EmailService emailService;
	private final UserRepository userRepository;

	public CustomLoginSuccessHandler(EmailService emailService, UserRepository userRepository) {
		this.emailService = emailService;
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String email = authentication.getName();
		Optional<User> userOpt = userRepository.findByEmail(email);
		userOpt.ifPresent(user -> emailService.sendLoginNotification(user.getEmail(), user.getName()));
		boolean isLibrarian = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_LIBRARIAN"));

        if (isLibrarian) {
            response.sendRedirect("/facade");
        } else {
            response.sendRedirect("/home");
        }

	}

}
