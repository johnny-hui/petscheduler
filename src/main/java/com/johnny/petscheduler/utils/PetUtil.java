package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.pet.PetDetails;
import com.johnny.petscheduler.entities.pet.PetTrait;
import com.johnny.petscheduler.entities.pet.PetTraitAssignment;
import com.johnny.petscheduler.entities.users.User;

import java.util.ArrayList;
import java.util.List;

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

    // TODO: Create a method that checks if pet is actually owned by the user before updating

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
        petDetails.setType(request.petDetails().type());
        petDetails.setSize(request.petDetails().size());
        petDetails.setGender(request.petDetails().gender());
        petDetails.setEnergyLevel(request.petDetails().energyLevel());

        // Set nullable fields
        if(request.petDetails().isVaccinated() != null) {
            petDetails.setIsVaccinated(request.petDetails().isVaccinated());
        }
        if(request.petDetails().hasBiteHistory() != null) {
            petDetails.setHasBiteHistory(request.petDetails().hasBiteHistory());
        }
        if(request.petDetails().canBeLeftAlone() != null) {
            petDetails.setCanBeLeftAlone(request.petDetails().canBeLeftAlone());
        }
        if(request.petDetails().profileDescription() != null) {
            petDetails.setProfileDescription(request.petDetails().profileDescription());
        }
        if(request.petDetails().extraNotes() != null) {
            petDetails.setExtraNotes(request.petDetails().extraNotes());
        }

        return petDetails;
    }

    private static List<PetTraitAssignment> parsePetTraits(Pet pet,
                                                           final PetRegistrationDTO request)
    {
        List<PetTraitAssignment> petTraits = new ArrayList<>();
        for(final PetTrait trait : request.petTraits()) {
            PetTraitAssignment petTraitAssignment = new PetTraitAssignment();
            petTraitAssignment.setPet(pet);
            petTraitAssignment.setTrait(trait);
            petTraits.add(petTraitAssignment);
        }
        return petTraits;
    }
}
