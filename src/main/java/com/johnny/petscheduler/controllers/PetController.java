package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.services.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.johnny.petscheduler.services.PetService.MAX_PETS_PER_OWNER;

@RestController
@RequestMapping("/api/users/pets")
public class PetController
{
    private final PetService petService;

    @Autowired
    public PetController(final PetService petService) {
        this.petService = petService;
    }

    /**
     * Creates and registers a Pet for a specific User.
     *
     * @param request
     *      A PetRegistrationDTO request
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return None
     */
    @PostMapping
    public ResponseEntity<Void> createPet(@Valid @RequestBody PetRegistrationDTO request,
                                          final Authentication authentication)
    {
        return petService.createPet(request, authentication);
    }

    /**
     * Bulk creates a list of Pets for a specific User.
     *
     * @param requests
     *      A list of PetRegistrationDTO requests
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return None (status OK)
     */
    @PostMapping("/bulk")
    public ResponseEntity<Void> createPets(
            @RequestBody
            @Size(min = 1, max = MAX_PETS_PER_OWNER, message = "Between 1 and 10 Pets must be provided")
            List<@Valid PetRegistrationDTO> requests,
            final Authentication authentication)
    {
        return petService.createPets(requests, authentication);
    }

    /**
     * Returns full information for a specific Pet.
     *
     * @param petID
     *      A long for the Pet ID
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return PetFullResponseDTO
     *      Full information about a Pet
     */
    @GetMapping("/{petID}")
    public ResponseEntity<PetFullResponseDTO> getPet(@PathVariable final long petID,
                                                     final Authentication authentication)
    {
        PetFullResponseDTO response = petService.getPet(petID, authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Returns a list of Pets for a specific User.
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return ResponseEntity<List<PetBaseResponseDTO>>
     *     A List of Pets (with limited information)
     */
    @GetMapping
    public ResponseEntity<List<PetBaseResponseDTO>> getAllPets(final Authentication authentication)
    {
        List<PetBaseResponseDTO> response = petService.getAllPetsFromUser(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    /**
     * Updates a Pet from DB.
     *
     * @param petID
     *      A Long for the Pet ID
     *
     * @param request
     *      A PetUpdateDTO object
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return None
     */
    @PutMapping("/{petID}")
    public ResponseEntity<Void> updatePet(@PathVariable final long petID,
                                          @Valid @RequestBody final PetUpdateDTO request,
                                          final Authentication authentication)
    {
        return petService.updatePet(petID, request, authentication);
    }

    /**
     * Deletes a Pet from DB.
     *
     * @param petID
     *      A Long for the Pet ID
     *
     * @param authentication
     *      An Authentication object from Spring Security
     *
     * @return None
     */
    @DeleteMapping("/{petID}")
    public ResponseEntity<Void> deletePet(@PathVariable final long petID,
                                          final Authentication authentication)
    {
        return petService.deletePet(petID, authentication);
    }
}
