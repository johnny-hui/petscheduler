package com.johnny.petscheduler.DTO.pet;


import com.johnny.petscheduler.entities.pet.PetTrait;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

/**
 * A record containing pet update information when updating a User's pet.
 *
 * @param name
 *      A String for the new name
 *
 * @param breed
 *      A String for the new breed
 *
 * @param dateOfBirth
 *      A LocalDate object
 *
 * @param avatarURL
 *      A String for the new avatar URL
 *
 * @param details
 *      A PetDetailsDTO object
 *
 * @param traits
 *      A set of PetTraits enum
 */
public record PetUpdateDTO (
        @Nullable
        @Size(max = 50, message = "Pet name must not exceed 50 characters")
        String name,

        @Nullable
        @Size(max = 100, message = "Breed must not exceed 100 characters")
        String breed,

        @Nullable
        @PastOrPresent(message = "Date of birth cannot be in the future")
        LocalDate dateOfBirth,

        @Nullable
        @URL(message = "A valid pet avatar URL must be provided")
        String avatarURL,

        @Nullable
        @Valid
        PetDetailsDTO details,

        @Nullable
        @Size(max = 5, message = "A pet cannot have more than 5 traits")
        Set<PetTrait> traits
) {}
