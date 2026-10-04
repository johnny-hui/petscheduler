package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.pet.*;
import com.johnny.petscheduler.entities.pet.*;
import com.johnny.petscheduler.entities.users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PetTestUtil
{
    private PetTestUtil() {}

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

    /**
     * An example PetRegistrationDTO object with all fields provided.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_ALL_FIELDS = new PetRegistrationDTO(
            "Tori",
            "American Eskimo",
            LocalDate.parse("2000-12-09"),
            "www.aws.com/23402",
            new PetDetailsRegistrationDTO(
                    PetType.DOG,
                    PetSize.SMALL,
                    PetGender.MALE,
                    EnergyLevel.HIGH,
                    true,
                    true,
                    true,
                    "I am tori",
                    "I need to be constantly monitored."
            ),
            new HashSet<>(Set.of(
                    PetTrait.AGGRESSIVE,
                    PetTrait.PROTECTIVE,
                    PetTrait.FOOD_DRIVEN
            ))
    );

    /**
     * A mock PetRegistrationDTO that contains several illegal characters for pet name.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_FAIL_REGEX_NAME = new PetRegistrationDTO(
            "Tori123",
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

    /**
     * A mock PetRegistrationDTO that contains several illegal characters for pet breed.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_FAIL_REGEX_BREED = new PetRegistrationDTO(
            "Tori",
            "American Eskimo1234",
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

    /**
     * A mock PetRegistrationDTO object that has several missing non-null (required) fields
     * in PetDetails.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_NON_NULL_FIELDS_MISSING_PET_DETAILS = new PetRegistrationDTO(
            "Tori",
            "American Eskimo",
            LocalDate.parse("2000-12-09"),
            null,
            new PetDetailsRegistrationDTO(
                    null,
                    PetSize.SMALL,
                    null,
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

    /**
     * A mock PetRegistrationDTO object that has several invalid enum values in PetDetails.
     */
    public static final String REGISTRATION_DTO_INVALID_ENUM_VALUES = """
              {
                  "name": "Tori",
                  "breed": "American Eskimo",
                  "dateOfBirth": "2000-12-09",
                  "avatarURL": null,
                  "details": {
                    "type": "HAMSTER",
                    "size": "SMALL",
                    "gender": "UNKNOWN1234",
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
            """
    ;

    /**
     * A mock PetRegistrationDTO object that has no name field.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_MISSING_NAME = new PetRegistrationDTO(
            null,
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

    /**
     * A mock PetRegistrationDTO object that is missing PetDetails.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_MISSING_PET_DETAILS = new PetRegistrationDTO(
            "Tori",
            "American Eskimo",
            LocalDate.parse("2000-12-09"),
            null,
            null,
            new HashSet<>(Set.of(
                    PetTrait.AGGRESSIVE,
                    PetTrait.PROTECTIVE,
                    PetTrait.FOOD_DRIVEN
            ))
    );

    /**
     * A mock PetRegistrationDTO object that is missing PetTraits.
     */
    public static final PetRegistrationDTO REGISTRATION_DTO_MISSING_PET_TRAITS = new PetRegistrationDTO(
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
         null
    );

    public static final PetRegistrationDTO REGISTRATION_DTO_MORE_THAN_FIVE_TRAITS = new PetRegistrationDTO(
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
                    PetTrait.FOOD_DRIVEN,
                    PetTrait.IRRITABLE,
                    PetTrait.ATTENTION_SEEKING,
                    PetTrait.INDEPENDENT
            ))
    );

    public static final PetUpdateDTO MOCK_UPDATE_DTO = new PetUpdateDTO(
            null,
            null,
            null,
            null,
            new PetDetailsUpdateDTO(
                    PetSize.MEDIUM,
                    null,
                    EnergyLevel.LOW,
                    true,
                    null,
                    null,
                    null,
                    null
            ),
            null
    );

    public static final PetUpdateDTO MOCK_UPDATE_DTO_ALL_FIELDS = new PetUpdateDTO(
            "Edson",
            "Yorkie",
            LocalDate.parse("2024-11-11"),
            "google-axis.com/1234",
            new PetDetailsUpdateDTO(
                    PetSize.MEDIUM,
                    PetGender.FEMALE,
                    EnergyLevel.LOW,
                    true,
                    false,
                    true,
                    "Edson is a beautiful yorkie!",
                    "N/A"
            ),
            new HashSet<>(Set.of(
                    PetTrait.CUDDLY,
                    PetTrait.MISCHIEVOUS,
                    PetTrait.TERRITORIAL,
                    PetTrait.ANXIOUS
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
     * Creates and returns a mock list of PetBaseResponseDTOs when a user requests for
     * getting all Pets owned by them.
     *
     * @return List<PetBaseResponseDTO>
     */
    public static List<PetBaseResponseDTO> createMockGetPetList(final int size)
    {
        List<PetBaseResponseDTO> list = new ArrayList<>();
        long id = 0L;
        for(int i = 0; i < size; i++) {
            list.add(
                new PetBaseResponseDTO(
                        ++id,
                        "Tori",
                        "American Eskimo",
                        LocalDate.parse("2000-12-09"),
                        null
            ));
        }
        return list;
    }

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

    public static Pet createMockPetForDBTests(final User owner) {
        Pet pet = new Pet();
        PetDetails petDetails = new PetDetails();

        pet.setOwner(owner);
        pet.setName("Tori");
        pet.setBreed("American Eskimo");
        pet.setDateOfBirth(LocalDate.parse("2000-12-09"));
        pet.setAvatarURL("https://avatars.google.com");

        petDetails.setSize(PetSize.SMALL);
        petDetails.setType(PetType.DOG);
        petDetails.setGender(PetGender.MALE);
        petDetails.setEnergyLevel(EnergyLevel.HIGH);
        pet.setDetails(petDetails);

        pet.setTraits(new HashSet<>(Set.of(
                PetTrait.AGGRESSIVE,
                PetTrait.FOOD_DRIVEN,
                PetTrait.ATTENTION_SEEKING
        )));

        return pet;
    }

    public static Pet createMockPet(final User owner) {
        Pet pet = new Pet();
        PetDetails petDetails = new PetDetails();

        pet.setId(1L);
        pet.setOwner(owner);
        pet.setName("Tori");
        pet.setBreed("American Eskimo");
        pet.setDateOfBirth(LocalDate.parse("2000-12-09"));
        pet.setAvatarURL("https://avatars.google.com");

        petDetails.setSize(PetSize.SMALL);
        petDetails.setType(PetType.DOG);
        petDetails.setGender(PetGender.MALE);
        petDetails.setEnergyLevel(EnergyLevel.HIGH);
        pet.setDetails(petDetails);

        pet.setTraits(new HashSet<>(Set.of(
                PetTrait.AGGRESSIVE,
                PetTrait.FOOD_DRIVEN,
                PetTrait.ATTENTION_SEEKING
        )));

        return pet;
    }
}
