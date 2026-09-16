package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.pet.Pet;
import jakarta.persistence.EntityManager;
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

    @Override
    public Optional<Pet> getPetById(final int petId) {
        return em.createQuery("SELECT p FROM Pet p WHERE p.id = :petId", Pet.class)
                .setParameter("petId", petId)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Pet> getAllPetsFromUser(final String email) {
        return em.createQuery("SELECT p FROM Pet p WHERE p.owner.email = :email ", Pet.class)
                .setParameter("email", email)
                .getResultList();
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
