package com.oops.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oops.library.repository.UserRepository;
import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.*;
import java.util.*;


@Service
public class UserInformationService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAllRegisteredUsers() throws EnchantedLibraryException
	{
		List<User> registeredUsers=userRepository.findAll();
		if(registeredUsers == null || registeredUsers.isEmpty())
		{
			throw new EnchantedLibraryException("There are no registered users at this moment");
		}
		return registeredUsers;
	}
	
	public User findByEmail(String email) throws EnchantedLibraryException
	{
		Optional<User> foundUser=userRepository.findByEmail(email);
		if(foundUser.isEmpty())
		{
			throw new EnchantedLibraryException("There is no user with this email");
		}
		return foundUser.get();
	}

}
