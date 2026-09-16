package com.johnny.petscheduler.DTO.pet;

import java.time.LocalDate;

/**
 * This record contains base information for a Pet whenever a user request to view
 * it for a specific pet.
 *
 * @param id
 *      A Long for the id
 *
 * @param name
 *      A String for the name of the pet
 *
 * @param dateOfBirth
 *      A LocalDate object for the DOB
 *
 * @param breed
 *      A String for the breed of the pet
 *
 * @param avatarURL
 *      A String for the avatarURL
 */
public record PetBaseResponseDTO(
        Long id,
        String name,
        String breed,
        LocalDate dateOfBirth,
        String avatarURL
) {}
