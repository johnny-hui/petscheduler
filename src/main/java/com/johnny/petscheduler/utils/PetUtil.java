package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.pet.PetDetails;
import com.johnny.petscheduler.entities.pet.PetTrait;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.PetNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class that contains methods that facilitate various Pet CRUD operations.
 */
public class PetUtil
{
    // Prevent instantiation
    private PetUtil() {}

    /**
     * Sets Pet information during Pet registration for a new Pet.
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
        pet.setDetails(createPetDetails(request));

        // Set PetTraits
        pet.setTraits(createPetTraits(request));
    }

    /**
     * Updates the information for a specific Pet.
     *
     * @param pet
     *      A Pet object
     *
     * @param request
     *      A PetUpdateDTO object
     */
    public static void updatePetInfo(Pet pet, final PetUpdateDTO request)
    {
        // Update any Base Pet Info
        updateBasePetInfo(pet, request);

        // Update any info from PetDetails
        if(request.details() != null) {
            updatePetDetails(pet, request);
        }

        // Update any info from PetTraits
        if(request.traits() != null && !request.traits().isEmpty()) {
            updatePetTraits(pet, request);
        }
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
        return new PetFullResponseDTO(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getDateOfBirth(),
                pet.getAvatarURL(),
                pet.getDetails(),
                new HashSet<>(pet.getTraits())
        );
    }

    /**
     * Validates a Pet (if exists) and returns the Pet.
     * If not found or does not belong to owner, throw an exception.
     *
     * @param petDAO
     *      A reference to petDAO implementation object
     *
     * @param petID
     *      A Long for the pet ID
     *
     * @param owner
     *      An authenticated User object
     *
     * @param httpRequestException
     *      The exception for type of HTTP request (if it should occur)
     *
     * @param exMessage
     *      The exception message (if Pet does not exist or does not belong to user)
     *
     * @return Pet (or exception)
     *      A Pet object
     */
    public static Pet validateAndGetPet(PetDAO petDAO,
                                        final long petID,
                                        final User owner,
                                        final String httpRequestException,
                                        final String exMessage)
    {
        return petDAO.getPetByIDAndOwnerID(petID, owner.getId())
                .orElseThrow(
                        () -> new PetNotFoundException(httpRequestException + exMessage)
                );
    }

    private static void updateBasePetInfo(Pet pet, final PetUpdateDTO request)
    {
        if(request.name() != null) {
            pet.setName(request.name());
        }
        if(request.breed() != null) {
            pet.setBreed(request.breed());
        }
        if(request.dateOfBirth() != null) {
            pet.setDateOfBirth(request.dateOfBirth());
        }
        if(request.avatarURL() != null) {
            pet.setAvatarURL(request.avatarURL());
        }
    }

    private static void updatePetDetails(Pet pet,
                                         final PetUpdateDTO request)
    {
        PetDetails petDetail = pet.getDetails();

        if(request.details().size() != null) {
            petDetail.setSize(request.details().size());
        }
        if(request.details().gender() != null) {
            petDetail.setGender(request.details().gender());
        }
        if(request.details().energyLevel() != null) {
            petDetail.setEnergyLevel(request.details().energyLevel());
        }
        if(request.details().isVaccinated() != null) {
            petDetail.setIsVaccinated(request.details().isVaccinated());
        }
        if(request.details().hasBiteHistory() != null) {
            petDetail.setHasBiteHistory(request.details().hasBiteHistory());
        }
        if(request.details().canBeLeftAlone() != null) {
            petDetail.setCanBeLeftAlone(request.details().canBeLeftAlone());
        }
        if(request.details().profileDescription() != null) {
            petDetail.setProfileDescription(request.details().profileDescription().trim());
        }
        if(request.details().extraNotes() != null) {
            petDetail.setExtraNotes(request.details().extraNotes().trim());
        }
    }

    private static void updatePetTraits(Pet pet, final PetUpdateDTO request)
    {
        // Get a reference (pointer) to set of PetTraits
        Set<PetTrait> petTraits = pet.getTraits();

        // Clear current all PetTraits from entity's collection (w/o replacing managed container)
        petTraits.clear();

        // Add new traits in
        petTraits.addAll(request.traits());
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

    private static PetDetails createPetDetails(final PetRegistrationDTO request)
    {
        PetDetails petDetails = new PetDetails();

        // Set Non-nullable (required) fields
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
            petDetails.setProfileDescription(request.details().profileDescription().trim());
        }
        if(request.details().extraNotes() != null) {
            petDetails.setExtraNotes(request.details().extraNotes().trim());
        }

        return petDetails;
    }

    private static Set<PetTrait> createPetTraits(final PetRegistrationDTO request)
    {
        return new HashSet<>(request.traits());
    }
}
