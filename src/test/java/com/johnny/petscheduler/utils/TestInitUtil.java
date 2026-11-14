package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.entities.users.*;
import com.johnny.petscheduler.security.user.UserRole;

/**
 * Contains helper methods that help facilitate initialization of different tests.
 */
public class TestInitUtil
{
    private TestInitUtil() {}

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

        assignBaseUserInfo(user, role);
        return user;
    }

    private static void assignBaseUserInfo(final User user, final UserRole role) {
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john_doe@abc.com");
        user.setPassword("password123");
        user.setCountry("US");
        user.setCity("Vancouver");
        user.setTimezone("America/Los_Angeles");
        user.setRole(role);
    }
}
