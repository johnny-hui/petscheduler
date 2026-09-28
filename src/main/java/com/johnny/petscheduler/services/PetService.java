package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.PetLimitExceededException;
import com.johnny.petscheduler.utils.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;

@Service
public class PetService
{
    public static final int MAX_PETS_PER_OWNER = 10;
    private final PetDAO petDAO;

    @Autowired
    public PetService(final PetDAO petDAO) {
        this.petDAO = petDAO;
    }

    // Create
    @Transactional
    public ResponseEntity<Void> createPet(final PetRegistrationDTO request,
                                          final Authentication authentication)
    {
        // Get User object after authentication has passed Spring Security Filters
        User owner = (User) authentication.getPrincipal();

        // Check if number of pets exceed limit (e.g. 10)
        if(petDAO.countPetsOwnedByUser(owner.getId()) >= MAX_PETS_PER_OWNER) {
            throw new PetLimitExceededException(
                    ERROR_MESSAGE_PET_COUNT_EXCEEDED + ": " + MAX_PETS_PER_OWNER
            );
        }

        // Create new pet, set info from request and persist
        Pet pet = new Pet();
        PetUtil.setPetInfo(pet, owner, request);
        petDAO.createPet(pet);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> createPets(final List<PetRegistrationDTO> requests,
                                           final Authentication authentication)
    {
        User owner = (User) authentication.getPrincipal();
        int listSize = requests.size();

        // Check if the total number of pets after addition of new pets exceeds the limit
        long petCount = petDAO.countPetsOwnedByUser(owner.getId());
        if((petCount + listSize) > MAX_PETS_PER_OWNER) {
            throw new PetLimitExceededException(
                    ERROR_MESSAGE_PET_BULK_CREATE_LIMIT_EXCEEDED + ": " + MAX_PETS_PER_OWNER
            );
        }

        // Perform bulk create + persist
        for(final PetRegistrationDTO request : requests) {
            Pet pet = new Pet();
            PetUtil.setPetInfo(pet, owner, request);
            petDAO.createPet(pet);
        }

        return ResponseEntity.ok().build();
    }

    // Read
    @Transactional(readOnly = true)
    public PetFullResponseDTO getPet(final long petID,
                                     final Authentication authentication)
    {
        User owner = (User) authentication.getPrincipal();

        Pet pet = PetUtil.validateAndGetPet(
                petDAO, petID, owner,
                ERROR_TITLE_PET_GET,
                ERROR_MESSAGE_PET_FETCH
        );

        return PetUtil.getPetFullInfo(pet);
    }

    @Transactional(readOnly = true)
    public List<PetBaseResponseDTO> getAllPetsFromUser(final Authentication authentication)
    {
        User owner = (User) authentication.getPrincipal();
        List<Pet> petList = petDAO.getAllPetsFromUser(owner.getId());
        List<PetBaseResponseDTO> response = new ArrayList<>();

        if(!petList.isEmpty()) {
            for(final Pet pet : petList) {
                response.add(new PetBaseResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getBreed(),
                        pet.getDateOfBirth(),
                        pet.getAvatarURL()
                ));
            }
        }
        return response;
    }

    // Update
    @Transactional
    public ResponseEntity<Void> updatePet(final int petID,
                                          final PetUpdateDTO request,
                                          final Authentication authentication)
    {
        // Get User details
        User owner = (User) authentication.getPrincipal();

        // Get Pet from DB and validate if pet is owned by the owner
        Pet pet = PetUtil.validateAndGetPet(
                petDAO, petID, owner,
                ERROR_TITLE_PET_UPDATE,
                ERROR_MESSAGE_PET_FETCH
        );

        // Replace any non-null values from request and add to Pet
        PetUtil.updatePetInfo(pet, request);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deletePet(final int petID,
                                          final Authentication authentication)
    {
        User owner = (User) authentication.getPrincipal();

        Pet pet = PetUtil.validateAndGetPet(
                petDAO, petID, owner,
                ERROR_TITLE_PET_DELETE,
                ERROR_MESSAGE_PET_FETCH
        );

        petDAO.deletePet(pet);
        return ResponseEntity.ok().build();
    }
}
