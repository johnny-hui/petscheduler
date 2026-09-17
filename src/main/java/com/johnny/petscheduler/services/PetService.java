package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.PetLimitExceededException;
import com.johnny.petscheduler.exceptions.PetNotFoundException;
import com.johnny.petscheduler.utils.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_PET_COUNT_EXCEEDED;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_PET_NOT_FOUND;

@Service
public class PetService
{
    private static final int MAX_PETS_PER_OWNER = 10;
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
        // Get User object after authentication has passed
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

    // Read
    @Transactional(readOnly = true)
    public PetFullResponseDTO getPet(final int petID,
                                     final Authentication authentication)
    {
        User owner = (User) authentication.getPrincipal();

        Pet pet = petDAO.getPetByIDAndOwnerID(petID, owner.getId())
                .orElseThrow(() -> new PetNotFoundException(ERROR_MESSAGE_PET_NOT_FOUND));

        return PetUtil.getPetFullInfo(pet);
    }

    @Transactional(readOnly = true)
    public List<PetBaseResponseDTO> getAllPetsFromUser(final Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();
        List<Pet> pets = petDAO.getAllPetsFromUser(user.getId());
        List<PetBaseResponseDTO> response = new ArrayList<>();

        for(final Pet pet : pets) {
            response.add(new PetBaseResponseDTO(
                    pet.getId(),
                    pet.getName(),
                    pet.getBreed(),
                    pet.getDateOfBirth(),
                    pet.getAvatarURL()
            ));
        }
        return response;
    }

    // Update
    @Transactional
    public ResponseEntity<Void> updatePet(final int petID,
                                          final PetUpdateDTO request,
                                          final Authentication authentication)
    {
        return ResponseEntity.ok().build();
    }

    // Delete
    @Transactional
    public ResponseEntity<Void> deletePet(final int petID,
                                          final Authentication authentication)
    {
        return ResponseEntity.ok().build();
    }
}
