package com.oops.library.service;

import com.oops.library.design.patterns.UserFactory;
import com.oops.library.dto.RegistrationDto;
import com.oops.library.entity.User;
import com.oops.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	public RegistrationService(UserRepository userRepository,
								 PasswordEncoder passwordEncoder,
								 EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

//	@Transactional
//	public void registerUser(RegistrationDto dto) {
//        User user = UserFactory.createUser(dto);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//		User persisted = userRepository.saveAndFlush(user);
//		log.info("Registered new user with id={} email={}", persisted.getId(), persisted.getEmail());
//		emailService.sendSignupConfirmation(persisted.getEmail(), persisted.getName());
//    }
//	@Transactional
//	public void registerUser(RegistrationDto dto) {
//	    User user = UserFactory.createUser(dto);
//	    user.setPassword(passwordEncoder.encode(user.getPassword()));
//	    User persisted = userRepository.saveAndFlush(user);
//	    log.info("Registered new user with id={} email={}", persisted.getId(), persisted.getEmail());
//	    
//	    boolean emailSent = emailService.sendSignupConfirmation(persisted.getEmail(), persisted.getName());
//	    if (!emailSent) {
//	        log.warn("Failed to send signup confirmation email to: {}", persisted.getEmail());
//	        // You might want to queue for retry or take other action
//	    }
//	}
	
	@Transactional
	public void registerUser(RegistrationDto dto) {
	    log.info("Starting registration process for email: {}", dto.getEmail());
	    
	    try {
	        User user = UserFactory.createUser(dto);
	        log.debug("User object created: {}", user.getEmail());
	        
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        log.debug("Password encoded successfully");
	        
	        User persisted = userRepository.saveAndFlush(user);
	        log.info("User saved to database with id: {}", persisted.getId());
	        
	        log.info("Attempting to send confirmation email to: {}", persisted.getEmail());
	        boolean emailSent = emailService.sendSignupConfirmation(persisted.getEmail(), persisted.getName());
	        
	        if (emailSent) {
	            log.info("Registration completed successfully for user: {}", persisted.getEmail());
	        } else {
	            log.warn("User registered but email failed to send for: {}", persisted.getEmail());
	        }
	        
	    } catch (Exception e) {
	        log.error("Registration failed for email: {}", dto.getEmail(), e);
	        throw e; // Re-throw to maintain transactional behavior
	    }
	}
}
