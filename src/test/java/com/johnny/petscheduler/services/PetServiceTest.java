package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.PetDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.entities.pet.Pet;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.PetLimitExceededException;
import com.johnny.petscheduler.exceptions.PetNotFoundException;
import com.johnny.petscheduler.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.johnny.petscheduler.utils.PetTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
public class PetServiceTest
{
    @Mock
    private PetDAO petDAO;

    @InjectMocks
    private PetService petService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void createPet_ShouldCreatePetSuccessfully()
    {
        // Arrange (create mock data)
        User owner = new PetOwner();
        owner.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        PetRegistrationDTO mockData = REGISTRATION_DTO;

        // Act
        petService.createPet(mockData, authentication);

        // Assert
        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petDAO).createPet(petCaptor.capture());
        Pet savedPet = petCaptor.getValue();

        assertInstanceOf(Pet.class, savedPet);
        assertEquals("Tori", savedPet.getName());
        assertEquals(owner, savedPet.getOwner());
    }

    @Test
    void createPet_WhenAllPetRegistrationDTOFieldsProvided_ShouldCreatePetSuccessfully()
    {
        // Arrange (create mock data)
        User owner = new PetOwner();
        owner.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.countPetsOwnedByUser(eq(owner.getId())))
                .thenReturn(1L); // set to owner has 1 pet

        PetRegistrationDTO mockData = REGISTRATION_DTO_ALL_FIELDS;

        // Act
        petService.createPet(mockData, authentication);

        // Assert
        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petDAO).createPet(petCaptor.capture());
        Pet savedPet = petCaptor.getValue();

        assertNotNull(savedPet);
        assertNotNull(savedPet.getName());
        assertNotNull(savedPet.getOwner());
        assertInstanceOf(Pet.class, savedPet);
        assertEquals("Tori", savedPet.getName());
        assertNotNull(savedPet.getOwner());
        assertNotNull(savedPet.getDetails());
        assertNotNull(savedPet.getTraits());
    }

    @Test
    void createPet_WhenUserHasExceededPetLimit_ShouldThrowPetLimitExceededException()
    {
        // Arrange (create mock data)
        User owner = new PetOwner();
        owner.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.countPetsOwnedByUser(eq(owner.getId())))
                .thenReturn(11L);

        PetRegistrationDTO mockData = REGISTRATION_DTO;

        // Act + Assert
        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        assertThrows(
                PetLimitExceededException.class,
                () -> petService.createPet(mockData, authentication)
        );
        verify(petDAO, never()).createPet(
                petCaptor.capture()
        );
    }

    @Test
    void createBulkPets_ShouldCreateAllPetSuccessfully()
    {
        // Arrange (create mock data)
        User owner = new PetOwner();
        owner.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.countPetsOwnedByUser(eq(owner.getId())))
                .thenReturn(1L);

        List<PetRegistrationDTO> mockData = createBulkPetList(5);

        // Act
        petService.createPets(mockData, authentication);

        // Assert
        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petDAO, times(5)).createPet(captor.capture());
        List<Pet> createdPets = captor.getAllValues();

        assertEquals(5, createdPets.size());
        assertEquals("Tori", createdPets.getFirst().getName());
        assertEquals("Tori", createdPets.get(1).getName());
        assertEquals("Tori", createdPets.get(2).getName());
        assertEquals("Tori", createdPets.get(3).getName());
        assertEquals("Tori", createdPets.getLast().getName());
    }

    @Test
    void createBulkPets_WhenNumberOfPetsInRequestExceedsLimit_ShouldThrowPetLimitExceededException()
    {
        // Arrange (create mock data)
        User owner = new PetOwner();
        owner.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.countPetsOwnedByUser(eq(owner.getId())))
                .thenReturn(6L);

        // Total number of pets = 11
        List<PetRegistrationDTO> mockData = createBulkPetList(5);

        // Act + Assert
        assertThrows(
                PetLimitExceededException.class,
                () -> petService.createPets(mockData, authentication)
        );
    }

    @Test
    void getPet_WhenPetExistsAndOwnedByOwner_ShouldReturnFullPetInfo()
    {
        // Arrange
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        Pet mockPet = createMockPet(owner);
        when(petDAO.getPetByIDAndOwnerID(
                eq(1L),
                eq(owner.getId())))
                .thenReturn(Optional.of(mockPet));

        // Act
        PetFullResponseDTO response = petService.getPet(mockPet.getId(), authentication);

        // Assert
        assertNotNull(response);
        assertInstanceOf(PetFullResponseDTO.class, response);
        assertEquals(1L, response.id());
        assertEquals("Tori", response.name());
        assertEquals("American Eskimo", response.breed());
        assertNotNull(response.details());
        assertNotNull(response.traits());
    }

    @Test
    void getPet_WhenPetDoesntExistOrIsNotOwnedByUser_ShouldThrowPetNotFoundException()
    {
        // Arrange
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.getPetByIDAndOwnerID(  // returns nothing if doesn't exist/not owned by user
                eq(1L),
                eq(owner.getId())))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                PetNotFoundException.class,
                () -> petService.getPet(1L, authentication)
        );
    }

    @Test
    void getAllPets_WhenRequestIsValid_ShouldReturnAllPetsInBasePetResponseDTO()
    {
        // Arrange (create 5 pets)
        List<Pet> petList = new ArrayList<>();
        User owner = new PetOwner();
        owner.setId(1L);
        for(int i = 0; i < 5; i++) {
            petList.add(createMockPet(owner));
        }

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.getAllPetsFromUser(eq(owner.getId())))
                .thenReturn(petList);

        // Act
        List<PetBaseResponseDTO> response = petService.getAllPetsFromUser(authentication);

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(5, response.size());
        assertInstanceOf(PetBaseResponseDTO.class, response.get(0));
        assertInstanceOf(PetBaseResponseDTO.class, response.get(1));
        assertInstanceOf(PetBaseResponseDTO.class, response.get(2));
        assertInstanceOf(PetBaseResponseDTO.class, response.get(3));
        assertInstanceOf(PetBaseResponseDTO.class, response.get(4));
        System.out.println(response);
    }

    @Test
    void getAllPets_WhenNoPetsAreOwnedByOwner_ShouldReturnEmptyList()
    {
        // Arrange
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.getAllPetsFromUser(
                eq(owner.getId()))
        ).thenReturn(Collections.emptyList());

        // Act
        List<PetBaseResponseDTO> response = petService.getAllPetsFromUser(authentication);

        // Assert
        verify(petDAO).getAllPetsFromUser(
                eq(owner.getId())
        );
        assertTrue(response.isEmpty());
    }

    @Test
    void updatePet_CertainFieldsWhenPetExistsAndOwnedByUser_ShouldUpdatePet()
    {
        // Arrange (pretend Pet is the one returned from PetDAO)
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        Pet pet =  createMockPet(owner); // EnergyLevel == High; PetSize == Small
        PetUpdateDTO request = MOCK_UPDATE_DTO;  // changes EnergyLevel to low and PetSize to med
        when(petDAO.getPetByIDAndOwnerID(
                eq(pet.getId()),
                eq(owner.getId())
        )).thenReturn(Optional.of(pet));

        // Act
        petService.updatePet(pet.getId(), request, authentication);

        // Assert (test if pet object has updated)
        assertNotNull(pet);
        assertEquals(pet.getDetails().getEnergyLevel(), request.details().energyLevel());
        assertEquals(pet.getDetails().getSize(), request.details().size());
    }

    @Test
    void updatePet_AllFieldsWhenPetExistsAndOwnedByUser_ShouldUpdatePet()
    {
        // Arrange (create mock Pet and PetUpdateDTO)
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        PetUpdateDTO request = MOCK_UPDATE_DTO_ALL_FIELDS;
        Pet pet =  createMockPet(owner);
        when(petDAO.getPetByIDAndOwnerID(
                eq(pet.getId()),
                eq(owner.getId())
        )).thenReturn(Optional.of(pet));

        // Act
        petService.updatePet(pet.getId(), request, authentication);

        // Arrange
        assertNotNull(pet);
        assertEquals(pet.getName(), request.name());
        assertEquals(pet.getBreed(), request.breed());
        assertEquals(pet.getDetails().getEnergyLevel(), request.details().energyLevel());
        assertEquals(pet.getDetails().getIsVaccinated(), request.details().isVaccinated());
        assertEquals(pet.getDetails().getSize(), request.details().size());
        assertEquals(pet.getTraits().size(), request.traits().size());
    }

    @Test
    void updatePet_WhenPetDoesNotExist_ShouldThrowPetNotFoundException()
    {
        // Arrange (simulate Pet not found)
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        PetUpdateDTO request = MOCK_UPDATE_DTO;
        when(petDAO.getPetByIDAndOwnerID(
                eq(1L),
                eq(owner.getId())
        )).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                PetNotFoundException.class,
                () -> petService.updatePet(1L, request, authentication)
        );
    }

    @Test
    void deletePet_WhenRequestIsValid_ShouldDeletePet()
    {
        // Arrange
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        Pet pet =  createMockPet(owner);
        when(petDAO.getPetByIDAndOwnerID(
                eq(pet.getId()),
                eq(owner.getId())
        )).thenReturn(Optional.of(pet));

        // Act
        petService.deletePet(pet.getId(), authentication);

        // Assert
        verify(petDAO).getPetByIDAndOwnerID(
                eq(pet.getId()),
                eq(owner.getId())
        );
        verify(petDAO).deletePet(pet);
    }

    @Test
    void deletePet_WhenPetDoesNotExist_ShouldThrowPetNotFoundException()
    {
        // Arrange (simulate no pet return)
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);
        when(petDAO.getPetByIDAndOwnerID(
                eq(1L),
                eq(owner.getId())
        )).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                PetNotFoundException.class,
                () -> petService.deletePet(1L, authentication)
        );
        verify(petDAO, never()).deletePet(
                any(Pet.class)
        );
    }

    @Test
    void getPet_WhenRequestIsValid_VerifyNoUserInformationInDTO()
    {
        // Arrange
        User owner = new PetOwner();
        owner.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(owner);

        Pet pet =  createMockPet(owner);
        when(petDAO.getPetByIDAndOwnerID(
                eq(pet.getId()),
                eq(owner.getId())
        )).thenReturn(Optional.of(pet));

        // Act
        PetFullResponseDTO response = petService.getPet(owner.getId(), authentication);
        String json = objectMapper.writeValueAsString(response);

        // Assert
        assertFalse(json.contains("\"owner\""));
        assertFalse(json.contains("\"user\""));
    }
}
