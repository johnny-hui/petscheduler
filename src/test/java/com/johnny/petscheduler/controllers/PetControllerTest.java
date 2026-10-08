package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.pet.PetBaseResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.DTO.pet.PetUpdateDTO;
import com.johnny.petscheduler.exceptions.PetNotFoundException;
import com.johnny.petscheduler.security.configs.SecurityConfig;
import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import com.johnny.petscheduler.security.user.CustomUserDetailsService;
import com.johnny.petscheduler.security.utils.JWTUtil;
import com.johnny.petscheduler.services.PetService;
import com.johnny.petscheduler.utils.PetTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static com.johnny.petscheduler.utils.PetTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@Import(SecurityConfig.class)
public class PetControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserDAO userDAO;

    @MockitoBean
    private AuthEntryPointJWT authEntryPointJWT;

    @Test
    void shouldDeserializeIntoPetRegistrationDTO() throws Exception
    {
        String requestJSON = PetTestUtil.STRING_REGISTRATION_DTO;
        assertDoesNotThrow(() ->
                objectMapper.readValue(requestJSON, PetRegistrationDTO.class)
        );
    }

    @Test
    void createPet_ShouldRejectUnauthenticatedUser() throws Exception
    {
        // All pet API endpoints require authentication
        mockMvc.perform(
                post("/api/users/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Buddy"
                                }
                                """)
                )
                .andExpect(status().isUnauthorized());

        verify(petService, never()).createPet(
                any(),
                any()
        );
    }

    @Test
    void updatePet_ShouldRejectUnauthenticatedUser() throws Exception
    {
        // Arrange (create mock data for Update Pet)
        PetUpdateDTO mockData = MOCK_UPDATE_DTO;
        String request = this.objectMapper.writeValueAsString(mockData);

        // Act
        mockMvc.perform(
                    put("/api/users/pets/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        // Assert
        verify(petService, never()).updatePet(
                eq(1),
                eq(mockData),
                any(Authentication.class)
        );
    }

    @Test
    void deletePet_ShouldRejectUnauthenticatedUser() throws Exception
    {
        // Arrange (Mock ID)
        int petID = 1;

        // Act
        MvcResult result = mockMvc.perform(delete("/api/users/pets/" + petID))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert
        verify(petService, never()).deletePet(
                eq(petID),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createPet_RequestHasRequiredFields_ShouldAllowToCreatePet() throws Exception
    {
        // Arrange
        String request = PetTestUtil.STRING_REGISTRATION_DTO;
        when(petService.createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                post("/api/users/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        // Print Response Debug Statements
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createBulkPet_WhenRequestIsValid_ShouldAllowToCreateAllPets() throws Exception
    {
        // Arrange
        List<PetRegistrationDTO> request = PetTestUtil.createBulkPetList(5);
        String stringedRequest = objectMapper.writeValueAsString(request);
        when(petService.createPets(
                eq(request),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stringedRequest)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).createPets(
                eq(request),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createBulkPet_WhenBulkPetsReceivedExceedsLimit_ShouldRejectRequest() throws Exception
    {
        // Arrange
        List<PetRegistrationDTO> request = PetTestUtil.createBulkPetList(15);
        String stringedRequest = objectMapper.writeValueAsString(request);
        when(petService.createPets(
                eq(request),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stringedRequest)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPets(
                eq(request),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createBulkPet_WhenBulkPetsReceivedIsEmpty_ShouldRejectRequest() throws Exception
    {
        // Arrange
        List<PetRegistrationDTO> request = new ArrayList<>();
        String stringedRequest = objectMapper.writeValueAsString(request);
        when(petService.createPets(
                eq(request),
                any(Authentication.class))
        ).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stringedRequest)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPets(
                eq(request),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createBulkPet_WhenOneOfThePetsInRequestIsMalformed_ShouldRejectRequest() throws Exception
    {
        // Arrange
        List<PetRegistrationDTO> request = PetTestUtil.createMalformedPetList(5);
        String stringedRequest = objectMapper.writeValueAsString(request);
        when(petService.createPets(
                eq(request),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stringedRequest)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPets(
                eq(request),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createPet_RequestHasMissingRequiredFields_ShouldRejectInvalidRequest() throws Exception
    {
        // Arrange (should fail bean validation in controller)
        String request = PetTestUtil.REGISTRATION_DTO_MISSING_REQUIRED_FIELDS;
        when(petService.createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        )).thenReturn(null);

        // Act
        MvcResult result = mockMvc.perform(
                            post("/api/users/pets")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                            )
                            .andExpect(status().isBadRequest())
                            .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(),
                any()
        );

        // Print Response Debug Statements
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    void getPet_WhenUnauthenticatedUser_ShouldRejectRequest() throws Exception
    {
        // Create a Mock PetResponseDTO
        PetFullResponseDTO response = PetTestUtil.GET_PET_RESPONSE_DTO;

        // Arrange
        when(petService.getPet(
                eq(1),
                any(Authentication.class)
        )).thenReturn(response);

        // Act
        MvcResult result = mockMvc.perform(get("/api/users/pets/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert
        verify(petService, never()).getPet(
                eq(1),
                any(Authentication.class)
        );

        // Print Response Debug Statements
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void getPet_WhenRequestIsValidAndUserIsAuthenticated_ShouldAllowRequest() throws Exception
    {
        // Create a Mock PetResponseDTO
        PetFullResponseDTO response = PetTestUtil.GET_PET_RESPONSE_DTO;

        // Arrange
        when(petService.getPet(
                eq(1L),
                any(Authentication.class)
        )).thenReturn(response);

        // Act
        MvcResult result = mockMvc.perform(get("/api/users/pets/1"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).getPet(
                eq(1L),
                any(Authentication.class)
        );

        // Print Response Debug Statements
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void getPet_WhenUserPassesInAPetIDButDoesntOwnThePet_ShouldThrowPetNotFoundException() throws Exception
    {
        // Arrange
        when(petService.getPet(
                eq(5L),
                any(Authentication.class)))
            .thenThrow(new PetNotFoundException("You do not have access to this pet"));

        // Act
        MvcResult result = mockMvc.perform(get("/api/users/pets/5"))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.errorMessage")
                        .value("You do not have access to this pet")
                )
                .andReturn();

        // Assert
        verify(petService).getPet(
                eq(5L),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void getPet_WhenNoPetIDExists_ShouldThrowPetNotFoundException() throws Exception
    {
        // Arrange
        when(petService.getPet(
                eq(999L),
                any(Authentication.class)))
                .thenThrow(
                        new PetNotFoundException("The pet with ID 999 does not exist!")
                );

        // Act
        MvcResult result = mockMvc.perform(
                get("/api/users/pets/999"))
                .andExpect(status().isNotFound())
                .andReturn();

        // Arrange
        verify(petService).getPet(
                eq(999L),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void getAllPets_WhenRequestIsValidAndUserIsAuthenticated_ShouldAllowRequestAndReturnList() throws Exception
    {
        // Arrange (Create Mock Data)
        List<PetBaseResponseDTO> mockPetList = createMockGetPetList(7);
        when(petService.getAllPetsFromUser(
                any(Authentication.class)))
                .thenReturn(mockPetList);

        // Act
        MvcResult result = mockMvc.perform(
                    get("/api/users/pets")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())  // Check object in index 0
                .andExpect(jsonPath("$[6]").exists())  // Check object in last index
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Tori"))
                .andExpect(jsonPath("$[6].id").value(7))
                .andExpect(jsonPath("$[6].name").value("Tori"))
                .andReturn();


        // Assert
        verify(petService).getAllPetsFromUser(
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void updatePet_WhenRequestIsValid_ShouldAllowRequestAndReturnOK() throws Exception
    {
        // Arrange (create Mock Data)
        final long petID = 60L;
        final PetUpdateDTO request = MOCK_UPDATE_DTO;
        when(petService.updatePet(
                eq(petID),
                eq(request),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                put("/api/users/pets/" + petID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).updatePet(
                eq(petID),
                eq(request),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void deletePet_WhenRequestIsValid_ShouldAllowRequestAndReturnOK() throws Exception
    {
        // Arrange
        final long petID = 60L;
        when(petService.deletePet(
                eq(petID),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        MvcResult result = mockMvc.perform(
                delete("/api/users/pets/" + petID))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).deletePet(
                eq(petID),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenMissingNameInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_MISSING_NAME;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenPetTraitsIsMissingInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_MISSING_PET_TRAITS;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenMoreThanFivePetTraitsInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_MORE_THAN_FIVE_TRAITS;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenMissingPetDetailsInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_MISSING_PET_DETAILS;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenOneOrMoreRequiredFieldsInPetDetailsIsMissingInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_NON_NULL_FIELDS_MISSING_PET_DETAILS;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void validationTest_WhenInvalidEnumInPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        String mockData = REGISTRATION_DTO_INVALID_ENUM_VALUES;

        // Act
        when(petService.createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mockData)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void regexTest_WhenNonAlphabetCharactersInPetNameFieldOfPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_FAIL_REGEX_NAME;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void regexTest_WhenNonAlphabetCharactersInPetBreedFieldOfPetRegistrationDTO_ShouldReturnBadRequest() throws Exception
    {
        // Arrange
        PetRegistrationDTO mockData = REGISTRATION_DTO_FAIL_REGEX_BREED;
        when(petService.createPet(
                eq(mockData),
                any(Authentication.class)
        )).thenReturn(ResponseEntity.badRequest().build());

        // Act
        MvcResult result = mockMvc.perform(
                        post("/api/users/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        verify(petService, never()).createPet(
                any(PetRegistrationDTO.class),
                any(Authentication.class)
        );

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

}
