package com.johnny.petscheduler.entity;

import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.pet.PetDetails;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.security.user.UserRole;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static com.johnny.petscheduler.utils.PetTestUtil.createMockPetForDBTests;
import static com.johnny.petscheduler.utils.TestInitUtil.createMockUser;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This tests DB constraints on the Pet entity.
 */
@DataJpaTest
public class PetPersistenceTest
{
    @Autowired
    private TestEntityManager entityManager;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = createMockUser(UserRole.PET_OWNER, "johndoe123@outlook.com");
        entityManager.persistAndFlush(owner);
        entityManager.clear();
    }

    @Test
    void persistPet_ValidPet_ShouldGenerateId()
    {
        Pet pet = createMockPetForDBTests(owner);
        entityManager.persistAndFlush(pet);
        assertNotNull(pet.getId());
    }

    @Test
    void persistPet_NullOwner_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setOwner(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NullName_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setName(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NameExceeds50Characters_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setName("A".repeat(51));

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_BreedExceeds100Characters_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setBreed("A".repeat(101));

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NullOptionalFields_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);

        pet.setDateOfBirth(null);
        pet.setBreed(null);
        pet.setAvatarURL(null);

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }

    @Test
    void persistPet_NameExactly50Characters_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setName("A".repeat(50));

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }

    @Test
    void persistPet_BreedExactly100Characters_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);
        pet.setBreed("A".repeat(100));

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }

    // ------------------------------------
    // Embedded PetDetails Required Fields
    // ------------------------------------
    @Test
    void persistPet_NullType_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setType(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NullSize_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setSize(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NullGender_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setGender(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_NullEnergyLevel_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setEnergyLevel(null);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    // ------------------------------------
    // Embedded PetDetails Optional Fields
    // ------------------------------------
    @Test
    void persistPet_NullOptionalDetails_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);
        PetDetails details = pet.getDetails();

        details.setIsVaccinated(null);
        details.setHasBiteHistory(null);
        details.setCanBeLeftAlone(null);
        details.setProfileDescription(null);
        details.setExtraNotes(null);

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }

    // ------------------------------------
    // Embedded PetDetails String Lengths
    // ------------------------------------

    @Test
    void persistPet_ProfileDescriptionExceeds1000_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setProfileDescription("A".repeat(1001));

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_ProfileDescriptionExactly1000_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setProfileDescription("A".repeat(1000));

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }

    @Test
    void persistPet_ExtraNotesExceeds1000_ShouldThrowException() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setExtraNotes("A".repeat(1001));

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistAndFlush(pet)
        );
    }

    @Test
    void persistPet_ExtraNotesExactly1000_ShouldPersistSuccessfully() {
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setExtraNotes("A".repeat(1000));

        entityManager.persistAndFlush(pet);

        assertNotNull(pet.getId());
    }
}