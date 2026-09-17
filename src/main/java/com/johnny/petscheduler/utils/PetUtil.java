package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.pet.PetDetails;
import com.johnny.petscheduler.entities.pet.PetTrait;
import com.johnny.petscheduler.entities.pet.PetTraitAssignment;
import com.johnny.petscheduler.entities.users.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A utility class that contains methods that facilitate various Pet CRUD operations.
 */
public class PetUtil
{
    // Prevent instantiation
    private PetUtil() {}

    /**
     * Sets Pet information from a PetRegistrationDTO on a new Pet.
     *
     * @param pet
     *      A Pet object
     *
     * @param owner
     *      An authenticated User object
     *
     * @param request
     *      A PetRegistrationDTO object
     */
    public static void setPetInfo(Pet pet,
                                  final User owner,
                                  final PetRegistrationDTO request)
    {
        // Set base pet info
        setBasePetInfo(pet, owner, request);

        // Create and set PetDetails
        pet.setDetails(createtPetDetails(request));

        // Set PetTraits
        pet.setTraits(parsePetTraits(pet, request));
    }

    /**
     * Parses Pet information and returns a PetFullResponseDTO.
     *
     * @param pet
     *      A Pet object
     *
     * @return PetFullResponseDTO
     *      A PetFullResponseDTO object
     */
    public static PetFullResponseDTO getPetFullInfo(final Pet pet)
    {
        Set<PetTrait> traits = new HashSet<>();
        for(final PetTraitAssignment petTrait : pet.getTraits()) {
            traits.add(petTrait.getTrait());
        }
        return new PetFullResponseDTO(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getDateOfBirth(),
                pet.getAvatarURL(),
                pet.getDetails(),
                traits
        );
    }

    private static void setBasePetInfo(Pet pet,
                                       final User owner,
                                       final PetRegistrationDTO request)
    {
        pet.setOwner(owner);
        pet.setName(request.name().trim());
        if(request.dateOfBirth() != null) {
            pet.setDateOfBirth(request.dateOfBirth());
        }
        if(request.breed() != null) {
            pet.setBreed(request.breed().trim());
        }
        if(request.avatarURL() != null) {
            pet.setAvatarURL(request.avatarURL().trim());
        }
    }

    private static PetDetails createtPetDetails(final PetRegistrationDTO request)
    {
        PetDetails petDetails = new PetDetails();

        // Set Non-nullable fields
        petDetails.setType(request.details().type());
        petDetails.setSize(request.details().size());
        petDetails.setGender(request.details().gender());
        petDetails.setEnergyLevel(request.details().energyLevel());

        // Set nullable fields
        if(request.details().isVaccinated() != null) {
            petDetails.setIsVaccinated(request.details().isVaccinated());
        }
        if(request.details().hasBiteHistory() != null) {
            petDetails.setHasBiteHistory(request.details().hasBiteHistory());
        }
        if(request.details().canBeLeftAlone() != null) {
            petDetails.setCanBeLeftAlone(request.details().canBeLeftAlone());
        }
        if(request.details().profileDescription() != null) {
            petDetails.setProfileDescription(request.details().profileDescription());
        }
        if(request.details().extraNotes() != null) {
            petDetails.setExtraNotes(request.details().extraNotes());
        }

        return petDetails;
    }

    private static List<PetTraitAssignment> parsePetTraits(Pet pet,
                                                           final PetRegistrationDTO request)
    {
        List<PetTraitAssignment> petTraits = new ArrayList<>();
        for(final PetTrait trait : request.traits()) {
            PetTraitAssignment petTraitAssignment = new PetTraitAssignment();
            petTraitAssignment.setPet(pet);
            petTraitAssignment.setTrait(trait);
            petTraits.add(petTraitAssignment);
        }
        return petTraits;
    }
}
