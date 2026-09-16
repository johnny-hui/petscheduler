package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.services.PetService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PetBaseResponseDTO> createPet(@Valid @RequestBody PetRegistrationDTO request,
                                                        final Authentication authentication)
    {
        return petService.createPet(request, authentication);
    }

    // Read
    @GetMapping
    public ResponseEntity<?> getAllPets(final Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body();
    }

    @GetMapping("/{petID}")
    public ResponseEntity<PetBaseResponseDTO> getPet(@PathVariable final int petID,
                                                     final Authentication authentication)
    {
        PetBaseResponseDTO response = petService.getPet(petID, authentication);
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
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Delete
    @DeleteMapping("/{petID}")
    public ResponseEntity<Void> deletePet(@PathVariable final int petID,
                                          final Authentication authentication)
    {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
