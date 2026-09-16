package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService
{
    private final PetDAO petDAO;

    @Autowired
    public PetService(final PetDAO petDAO) {
        this.petDAO = petDAO;
    }

    // Create
    public ResponseEntity<PetBaseResponseDTO> createPet(final PetRegistrationDTO request,
                                                        final Authentication authentication)
    {
        return ResponseEntity.ok().build();
    }


    // Read
    public PetBaseResponseDTO getPet(final int id, final Authentication authentication)
    {

    }

    public List<PetBaseResponseDTO> getAllPetsFromUser(final Authentication authentication)
    {

    }

    // Update

    // Delete

}
