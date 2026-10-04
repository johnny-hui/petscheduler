package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.entities.users.*;
import com.johnny.petscheduler.security.user.UserRole;

public class UserTestUtil
{
    private UserTestUtil() {}

    public static User createMockUser(final UserRole role)
    {
        User user = null;

        // Create the user
        switch(role)
        {
            case UserRole.PET_OWNER -> {
                user = new PetOwner();
                PetOwner owner = (PetOwner) user; // downcast
                owner.setBio("Hello test");
            }
            case UserRole.PET_SITTER -> {
                user = new PetSitter();
                PetSitter sitter = (PetSitter) user; // downcast
                sitter.setYearsOfExperience(2);
            }
            case UserRole.PET_BUSINESS -> {
                user = new PetBusiness();
                PetBusiness business = (PetBusiness) user; // downcast
                business.setBusinessName("Tori & Co.");
                business.setBusinessType(BusinessType.DOG_GROOMING);
                business.setAddress("1234 Donut Ave.");
                business.setCity("Vancouver");
                business.setEstablishmentYear(1995);
            }
        }

        // Assign base user info
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john_doe@abc.com");
        user.setPassword("password123");
        user.setPostalCode("ABC 123");
        user.setRole(role);

        return user;
    }
}
