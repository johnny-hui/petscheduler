package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.services.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/pets")
public class PetController
{
    private final PetService petService;

    @Autowired
    public PetController(final PetService petService) {
        this.petService = petService;
    }

    // Create
    @PostMapping
    public ResponseEntity<Void> createPet(@Valid @RequestBody PetRegistrationDTO request,
                                                        final Authentication authentication)
    {
        return petService.createPet(request, authentication);
    }

    // Read
    @GetMapping("/{petID}")
    public ResponseEntity<PetFullResponseDTO> getPet(@PathVariable final int petID,
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

    // Update
    @PutMapping("/{petID}")
    public ResponseEntity<Void> updatePet(@PathVariable final int petID,
                                          @Valid @RequestBody final PetUpdateDTO request,
                                          final Authentication authentication)
    {
        return petService.updatePet(petID, request, authentication);
    }

    // Delete
    @DeleteMapping("/{petID}")
    public ResponseEntity<Void> deletePet(@PathVariable final int petID,
                                          final Authentication authentication)
    {
        return petService.deletePet(petID, authentication);
    }
}
