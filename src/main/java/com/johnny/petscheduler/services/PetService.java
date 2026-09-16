package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
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
import java.util.List;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_PET_COUNT_EXCEEDED;

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
    public ResponseEntity<PetBaseResponseDTO> createPet(final PetRegistrationDTO request,
                                                        final Authentication authentication)
    {
        // Get User object after authentication has passed
        User owner = (User) authentication.getPrincipal();

        // Check if number of pets exceed limit (e.g. 10)
        if(petDAO.countPetsByUser(owner.getId()) >= MAX_PETS_PER_OWNER) {
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
    public PetBaseResponseDTO getPet(final int petID,
                                     final Authentication authentication)
    {

    }

    public List<PetBaseResponseDTO> getAllPetsFromUser(final Authentication authentication)
    {
        return null;
    }

    // Update
    public ResponseEntity<Void> updatePet(final int petID,
                                          final PetUpdateDTO request,
                                          final Authentication authentication)
    {
        return ResponseEntity.ok().build();
    }

    // Delete
    public ResponseEntity<Void> deletePet(final int petID,
                                          final Authentication authentication)
    {
        return ResponseEntity.ok().build();
    }
}
