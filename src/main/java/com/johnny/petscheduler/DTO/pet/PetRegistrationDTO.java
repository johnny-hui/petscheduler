package com.johnny.petscheduler.DTO.pet;

import com.johnny.petscheduler.entities.pet.PetTrait;
import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

/**
 * A record that is used to bind Pet information for the registration of a Pet.
 *
 * @implNote Example JSON preview
 *      {
 *          "name": "Buddy",
 *          "breed": "Golden Retriever",
 *          "dateOfBirth": "YYYY-MM-DD"
 *          "details": {
 *              "color": "Golden",
 *              "weight": 30.5,
 *              "sex": "MALE"
 *          },
 *          "traits": [
 *              "FRIENDLY",
 *              "ENERGETIC",
 *              "PLAYFUL"
 *          ]
 *      }
 */
public record PetRegistrationDTO(
        @NotBlank(message = "Pet name is required")
        @Size(max = 50, message = "Pet name must not exceed 50 characters")
        @Pattern(
                regexp = RegexUtil.ALPHABET_ONLY,
                message = "Pet name must contain only alphabetic characters"
        )
        String name,

        @Size(max = 100, message = "Breed must not exceed 100 characters")
        @Pattern(
                regexp = RegexUtil.ALPHABET_ONLY,
                message = "Breed must contain only alphabetic characters"
        )
        String breed,

        @Nullable
        @PastOrPresent(message = "Date of birth cannot be in the future")
        LocalDate dateOfBirth,

        @Nullable
        @URL(message = "A valid pet avatar URL must be provided")
        String avatarURL,

        @NotNull(message = "Pet details are required")
        @Valid  // => tells Spring to validate this DTO class too
        PetDetailsRegistrationDTO details,

        @NotEmpty(message = "A few pet traits are required")
        @Size(max = 5, message = "A pet cannot have more than 5 traits")
        Set<PetTrait> traits
) {}
