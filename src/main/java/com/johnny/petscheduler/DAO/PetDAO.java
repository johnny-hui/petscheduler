package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.pet.Pet;

import java.util.List;
import java.util.Optional;

public interface PetDAO
{
    // Create
    void createPet(final Pet pet);

    // Read
    Optional<Pet> getPetByIDAndOwnerID(final long petID, final long ownerID);
    Optional<Pet> getPetById(final long petId);
    List<Pet> getAllPetsFromUser(final long userID);
    long countPetsOwnedByUser(final long userID);

    // Update
    void updatePet(final Pet pet);

    // Delete
    void deletePet(final Pet pet);
}
