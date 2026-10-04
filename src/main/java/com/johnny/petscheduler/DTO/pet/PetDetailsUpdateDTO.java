package com.johnny.petscheduler.DTO.pet;

import com.johnny.petscheduler.entities.pet.EnergyLevel;
import com.johnny.petscheduler.entities.pet.PetGender;
import com.johnny.petscheduler.entities.pet.PetSize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

/**
 * A record containing updatable information for PetDetails.
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
public record PetDetailsUpdateDTO (
        @Nullable
        PetSize size,

        @Nullable
        PetGender gender,

        @Nullable
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
) {
}
