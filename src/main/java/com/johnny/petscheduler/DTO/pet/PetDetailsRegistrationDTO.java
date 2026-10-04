package com.johnny.petscheduler.DTO.pet;

import com.johnny.petscheduler.entities.pet.EnergyLevel;
import com.johnny.petscheduler.entities.pet.PetGender;
import com.johnny.petscheduler.entities.pet.PetSize;
import com.johnny.petscheduler.entities.pet.PetType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A record containing information for PetDetails.
 *
 * @param type
 *      A value from PetType enum
 *
 * @param size
 *      A value from PetSize enum
 *
 * @param gender
 *      A value from PetGender enum
 *
 * @param energyLevel
 *      A value from EnergyLevel enum
 *
 * @param isVaccinated
 *      A boolean for is pet vaccinated
 *
 * @param hasBiteHistory
 *      A boolean for whether the pet has biting tendencies
 *
 * @param canBeLeftAlone
 *      A boolean for whether the pet can be left alone
 *
 * @param profileDescription
 *      A String for the profile description
 *
 * @param extraNotes
 *      A String for extra notes about the pet
 */
public record PetDetailsRegistrationDTO(
        @NotNull(message = "Pet type is required")
        PetType type,

        @NotNull(message = "Pet size is required")
        PetSize size,

        @NotNull(message = "Pet gender is required")
        PetGender gender,

        @NotNull(message = "Pet energy level is required")
        EnergyLevel energyLevel,

        @Nullable
        Boolean isVaccinated,

        @Nullable
        Boolean hasBiteHistory,

        @Nullable
        Boolean canBeLeftAlone,

        @Nullable
        @Size(max = 1000, message = "Pet profile description must not exceed 1000 characters")
        String profileDescription,

        @Nullable
        @Size(max = 1000, message = "Extra notes for your pet must not exceed 1000 characters")
        String extraNotes
) {}
