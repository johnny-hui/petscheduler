package com.johnny.petscheduler.constants;

import com.johnny.petscheduler.DTO.pet.PetDetailsRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.entities.pet.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PetTestConstants
{
    private PetTestConstants() {}

    /**
     * An example PetRegistrationDTO object.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO = new PetRegistrationDTO(
            "Tori",
            "American Eskimo",
            LocalDate.parse("2000-12-09"),
            null,
            new PetDetailsRegistrationDTO(
                    PetType.DOG,
                    PetSize.SMALL,
                    PetGender.MALE,
                    EnergyLevel.HIGH,
                    null,
                    null,
                    null,
                    null,
                    null
            ),
            new HashSet<>(Set.of(
                    PetTrait.AGGRESSIVE,
                    PetTrait.PROTECTIVE,
                    PetTrait.FOOD_DRIVEN
            ))
    );

    public static final PetRegistrationDTO REGISTRATION_DTO_MISSING_FIELDS = new PetRegistrationDTO(
            null,
            null,
            LocalDate.parse("2000-12-09"),
            null,
            new PetDetailsRegistrationDTO(
                    PetType.DOG,
                    PetSize.SMALL,
                    PetGender.MALE,
                    EnergyLevel.HIGH,
                    null,
                    null,
                    null,
                    null,
                    null
            ),
            new HashSet<>(Set.of(
                    PetTrait.AGGRESSIVE,
                    PetTrait.PROTECTIVE,
                    PetTrait.FOOD_DRIVEN
            ))
    );

    public static final PetFullResponseDTO GET_PET_RESPONSE_DTO = new PetFullResponseDTO(
            1L,
            "Tori",
            "American Eskimo",
            LocalDate.parse("2000-12-09"),
            null,
            new PetDetails(),
            new HashSet<>(Set.of(
                    PetTrait.AGGRESSIVE,
                    PetTrait.PROTECTIVE,
                    PetTrait.FOOD_DRIVEN
            ))
    );

    /**
     * This represents a String version of PetRegistration DTO.
     */
    public static final String STRING_REGISTRATION_DTO = """
              {
                  "name": "Tori",
                  "breed": "American Eskimo",
                  "dateOfBirth": "2000-12-09",
                  "avatarURL": null,
                  "details": {
                    "type": "DOG",
                    "size": "SMALL",
                    "gender": "MALE",
                    "energyLevel": "HIGH",
                    "isVaccinated": null,
                    "hasBiteHistory": null,
                    "canBeLeftAlone": null,
                    "profileDescription": null,
                    "extraNotes": null
                  },
                  "traits": [
                    "PROTECTIVE",
                    "FOOD_DRIVEN",
                    "AGGRESSIVE"
                  ]
            }
            """;

    public static final String REGISTRATION_DTO_MISSING_REQUIRED_FIELDS = """
              {
                  "breed": "American Eskimo",
                  "dateOfBirth": "2000-12-09",
                  "avatarURL": null,
                  "details": {
                    "type": "DOG",
                    "size": "SMALL",
                    "gender": "MALE",
                    "energyLevel": "HIGH",
                    "isVaccinated": null,
                    "hasBiteHistory": null,
                    "canBeLeftAlone": null,
                    "profileDescription": null,
                    "extraNotes": null
                  }
            }
            """;

    /**
     * Returns a bulk list of PetRegistrationDTO objects.
     *
     * @param size
     *      An integer for the size of the list
     *
     * @return list
     */
    public static List<PetRegistrationDTO> createBulkPetList(final int size)
    {
        List<PetRegistrationDTO> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            list.add(REGISTRATION_DTO);
        }
        return list;
    }

    /**
     * Returns a List<PetRegistrationDTO> that contains a malformed at index 2.
     *
     * @param size
     *      An integer for the size of the list
     *
     * @return list
     */
    public static List<PetRegistrationDTO> createMalformedPetList(final int size)
    {
        List<PetRegistrationDTO> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            if(i == 2) {
                list.add(REGISTRATION_DTO_MISSING_FIELDS);
                continue;
            }
            list.add(REGISTRATION_DTO);
        }
        return list;
    }
}
