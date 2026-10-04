package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.PetschedulerApplication;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.pet.PetTrait;
import com.johnny.petscheduler.entities.users.User;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static com.johnny.petscheduler.security.user.UserRole.PET_OWNER;
import static com.johnny.petscheduler.utils.PetTestUtil.createMockPetForDBTests;
import static com.johnny.petscheduler.utils.UserTestUtil.createMockUser;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PetDAOImpl.class)
@ContextConfiguration(classes = PetschedulerApplication.class)
public class PetDAOImplTest
{
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetDAOImpl petDAO;

    // Test 1: Create and Persist
    @Test
    void createPet_ShouldPersistPet()
    {
        // Arrange (create mock data with a User in DB first)
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);

        // Act
        petDAO.createPet(pet);

        // Assert (pet should have ID of 1)
        Pet result = entityManager.find(Pet.class, 1L);
        assertNotNull(result);
        assertEquals(pet.getName(), result.getName());
    }

    // Test 1b: Creating a Pet with null values
    @Test
    void createPet_WithAllNullValues_ShouldThrowDBConstraintViolationException()
    {
        // Arrange
        Pet pet = new Pet();

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WithNoName_ShouldThrowDBConstraintViolationException()
    {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.setName(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WithNoOwner_ShouldThrowDBConstraintViolationException()
    {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.setOwner(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenBreedExceedLength_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.setBreed("***".repeat(1000));

        // Act + Assert
        assertThrows(
                DataException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenNullPetTypeInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setType(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenNullPetSizeInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setSize(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenNullPetGenderInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setGender(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenNullEnergyLevelInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setEnergyLevel(null);

        // Act + Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenLengthExceededProfileDescInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setProfileDescription("***".repeat(1000));

        // Act + Assert
        assertThrows(
                DataException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    @Test
    void createPet_WhenLengthExceededExtraNotesInPetDetails_ShouldThrowDBConstraintViolationException() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        pet.getDetails().setExtraNotes("***".repeat(1000));

        // Act + Assert
        assertThrows(
                DataException.class,
                () -> {
                    petDAO.createPet(pet);
                }
        );
    }

    // Test 2: Successfully Read (Get a Pet)
    @Test
    void getPet_WhenPetExists_ShouldReturnPet()
    {
        // Arrange (Create a mock user and a pet in DB)
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet = createMockPetForDBTests(owner);
        petDAO.createPet(pet);

        // Act
        Optional<Pet> result = petDAO.getPetByIDAndOwnerID(pet.getId(), owner.getId());

        // Assert
        assertEquals(pet, result.orElse(null));
    }

    // Test 3: Try to Get a Pet not owned by owner
    @Test
    void getPet_WhenPetNotOwnedByOwner_ShouldReturnEmptyOptional()
    {
        // Arrange (create two Pets owned by two different owners)
        User owner1 = createMockUser(PET_OWNER);
        owner1.setEmail("john123@abc.com");
        User owner2 = createMockUser(PET_OWNER);
        owner2.setEmail("joe123@abc.com");
        Pet pet1 = createMockPetForDBTests(owner1);
        Pet pet2 = createMockPetForDBTests(owner2);

        entityManager.persistAndFlush(owner1);
        entityManager.persistAndFlush(owner2);
        entityManager.persistAndFlush(pet1);
        entityManager.persistAndFlush(pet2);

        // Act
        Optional<Pet> result = petDAO.getPetByIDAndOwnerID(pet1.getId(), owner2.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    // Test 3b: Getting all pets owned by the owner
    @Test
    void getAllPets_WhenPetExistsAndOwnedByUser_ShouldReturnAllPets() {
        // Arrange (create 1 user + 5 mock pets)
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        for(int i = 0; i < 5; i++) {
            Pet pet = createMockPetForDBTests(owner);
            entityManager.persistAndFlush(pet);
        }

        // Act
        List<Pet> result = petDAO.getAllPetsFromUser(owner.getId());

        // Assert
        assertEquals(5, result.size());
        assertEquals(owner.getId(), result.get(0).getOwner().getId());
        assertEquals(owner.getId(), result.get(1).getOwner().getId());
        assertEquals(owner.getId(), result.get(2).getOwner().getId());
        assertEquals(owner.getId(), result.get(3).getOwner().getId());
        assertEquals(owner.getId(), result.get(4).getOwner().getId());
    }

    // Test 3c: Getting pets owned by owner when they do not have any pets
    @Test
    void getAllPets_WhenPetNotOwnedByUser_ShouldReturnEmptyList() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);

        // Act
        List<Pet> result = petDAO.getAllPetsFromUser(owner.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    // Test 4: Getting a Pet that does not exist
    @Test
    void getPet_WhenPetDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange (create 1 mock pet and user)
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);
        Pet pet =  createMockPetForDBTests(owner);
        entityManager.persistAndFlush(pet);

        // Act (get PetID with 333 which doesn't exist)
        Optional<Pet> result = petDAO.getPetByIDAndOwnerID(333L, owner.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    // Test 5: Successfully updating a Pet
    @Test
    void updatePet_ShouldUpdatePet() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        entityManager.persistAndFlush(owner);

        Pet pet = createMockPetForDBTests(owner);
        pet.setName("Dock");
        pet.setBreed("Pomeranian");
        entityManager.persistAndFlush(pet);
        entityManager.clear();  // => removes the managed entity from persistence context

        // Act
        Pet updatedPet = entityManager.find(Pet.class, 1L);
        updatedPet.setName("Charlie");
        updatedPet.setBreed("Yorkie");
        petDAO.updatePet(updatedPet);

        // Assert
        assertNotEquals("Dock", updatedPet.getName());
        assertNotEquals("Pomeranian", updatedPet.getBreed());
        assertEquals("Charlie", updatedPet.getName());
        assertEquals("Yorkie", updatedPet.getBreed());
    }

    // Test 6: Successfully deleting a Pet
    @Test
    void deletePet_ShouldDeletePet() {
        // Arrange
        User owner = createMockUser(PET_OWNER);
        Pet pet = createMockPetForDBTests(owner);
        entityManager.persistAndFlush(owner);
        entityManager.persistAndFlush(pet);
        entityManager.clear();

        // Act (Delete pet and Try to Retrieve Deleted Pet)
        Pet petToBeDeleted = petDAO.getPetByIDAndOwnerID(pet.getId(), owner.getId()).get();
        petDAO.deletePet(petToBeDeleted);
        Optional<Pet> result = petDAO.getPetByIDAndOwnerID(pet.getId(), owner.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    // Test 7: Reading PetTraits from a Pet (which is a separate table)
    @Test
    void getPet_WhenAccessingPetTraits_ShouldHaveAccessToPetTraits() {
        // Arrange (pet has the below PetTraits)
        User owner = createMockUser(PET_OWNER);
        Pet pet = createMockPetForDBTests(owner);
        entityManager.persistAndFlush(owner);
        entityManager.persistAndFlush(pet);
        entityManager.clear();

        // Act
        Pet result =  petDAO.getPetByIDAndOwnerID(pet.getId(), owner.getId()).get();

        // Assert
        assertNotNull(result.getTraits());
        assertFalse(result.getTraits().isEmpty());
        assertTrue(result.getTraits().contains(PetTrait.AGGRESSIVE));
        assertTrue(result.getTraits().contains(PetTrait.ATTENTION_SEEKING));
        assertTrue(result.getTraits().contains(PetTrait.FOOD_DRIVEN));
    }

    @Test
    void getPet_WhenAccessingUser_ShouldHaveAccessToUser() {
        // Arrange (pet has the below PetTraits)
        User owner = createMockUser(PET_OWNER);
        Pet pet = createMockPetForDBTests(owner);
        entityManager.persistAndFlush(owner);
        entityManager.persistAndFlush(pet);
        entityManager.clear();

        // Act
        Pet result =  petDAO.getPetByIDAndOwnerID(pet.getId(), owner.getId()).get();
        User fetchedUser = result.getOwner();

        // Arrange
        assertEquals("john_doe@abc.com", fetchedUser.getEmail());
        assertEquals("John", fetchedUser.getFirstName());
        assertEquals("Doe", fetchedUser.getLastName());
    }
}
