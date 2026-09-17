package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.pet.Pet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PetDAOImpl implements PetDAO
{
    private final EntityManager em;

    @Autowired
    public PetDAOImpl(final EntityManager em) {
        this.em = em;
    }

    @Override
    public void createPet(final Pet pet) {
        em.persist(pet);
    }

    /**
     * Returns a Pet owned by an owner, null if none found or pet doesn't
     * belong to owner.
     *
     * @param petID
     *      A Long for the pet ID
     *
     * @param ownerID
     *      A Long for the owner ID
     *
     * @return Pet or null
     */
    @Override
    public Optional<Pet> getPetByIDAndOwnerID(final long petID, final long ownerID) {
        TypedQuery<Pet> query = em.createQuery(
                "SELECT p FROM Pet p WHERE p.id = :petID AND p.owner.id = :ownerID ", Pet.class
        );
        query.setParameter("petID", petID);
        query.setParameter("ownerID", ownerID);
        return query.getResultStream().findFirst();
    }

    /**
     * Returns a Pet from the database according to a pet ID (no auth).
     *
     * @param petId
     *      A Long for the pet ID
     *
     * @return Pet or null
     */
    @Override
    public Optional<Pet> getPetById(final long petId) {
        return em.createQuery("SELECT p FROM Pet p WHERE p.id = :petId", Pet.class)
                .setParameter("petId", petId)
                .getResultList()
                .stream()
                .findFirst();
    }

    /**
     * Get all Pets for a specific owner.
     *
     * @param ownerID
     *      A Long for the user ID
     *
     * @return A List of Pets
     */
    @Override
    public List<Pet> getAllPetsFromUser(final long ownerID) {
        return em
                .createQuery("SELECT p FROM Pet p WHERE p.owner.id = :ownerID ", Pet.class)
                .setParameter("ownerID", ownerID)
                .getResultList();
    }

    /**
     * Returns the number of Pets owned by a specific user.
     *
     * @param userID
     *      A Long representing the user ID
     *
     * @return long
     *      The number of pets
     */
    @Override
    public long countPetsOwnedByUser(final long userID) {
        return em
                .createQuery("SELECT COUNT(p) FROM Pet p WHERE p.owner.id = :userID", Long.class)
                .setParameter("userID", userID)
                .getSingleResult();
    }

    @Override
    public void updatePet(final Pet pet) {
        em.merge(pet);
    }

    @Override
    public void deletePet(final Pet pet) {
        em.remove(pet);
    }
}
