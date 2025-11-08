package com.oops.library.design.patterns;

import com.oops.library.dto.RegistrationDto;
import com.oops.library.entity.*;

public class UserFactory {

    public static User createUser(RegistrationDto dto) {
        String role = dto.getRole().toUpperCase();

        return switch (role) {
            case "LIBRARIAN" -> {
                Librarian librarian = new Librarian();
                yield populateUserFields(librarian, dto, Role.LIBRARIAN);
            }
            case "SCHOLAR" -> {
                Scholar scholar = new Scholar();
                yield populateUserFields(scholar, dto, Role.SCHOLAR);
            }
            case "GUEST" -> {
                Guest guest = new Guest();
                yield populateUserFields(guest, dto, Role.GUEST);
            }
            default -> throw new IllegalArgumentException("Invalid role selected");
        };
    }

    private static User populateUserFields(User user, RegistrationDto dto, Role role) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword()); // password will be encoded in service
		user.setRole(role);
		user.setProfileImagePath(dto.getProfileImagePath());
        return user;
    }
}
