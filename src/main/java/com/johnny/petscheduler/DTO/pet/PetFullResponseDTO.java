package com.johnny.petscheduler.DTO.pet;

import com.johnny.petscheduler.entities.pet.PetDetails;
import com.johnny.petscheduler.entities.pet.PetTrait;

import java.time.LocalDate;
import java.util.Set;

/**
 * A record containing full Pet information for a specific pet.
 */
public record PetFullResponseDTO(
        Long id,
        String name,
        String breed,
        LocalDate dateOfBirth,
        String avatarURL,
        PetDetails details,
        Set<PetTrait> traits
) {}
