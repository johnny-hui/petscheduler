package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.pet.PetFullResponseDTO;
import com.johnny.petscheduler.DTO.pet.PetRegistrationDTO;
import com.johnny.petscheduler.constants.PetTestConstants;
import com.johnny.petscheduler.security.SecurityConfig;
import com.johnny.petscheduler.services.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void shouldDeserializeIntoPetRegistrationDTO() throws Exception
    {
        String requestJSON = PetTestConstants.STRING_REGISTRATION_DTO;
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
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void createPet_RequestHasRequiredFields_ShouldAllowToCreatePet() throws Exception
    {
        // Arrange
        String request = PetTestConstants.STRING_REGISTRATION_DTO;
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
    void createPet_RequestHasMissingRequiredFields_ShouldRejectInvalidRequest() throws Exception
    {
        // Arrange (should fail bean validation in controller)
        String request = PetTestConstants.REGISTRATION_DTO_MISSING_REQUIRED_FIELDS;
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
        PetFullResponseDTO response = PetTestConstants.GET_PET_RESPONSE_DTO;

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
    void getPet_WhenUserIsAuthenticated_ShouldAllowRequest() throws Exception
    {
        // Create a Mock PetResponseDTO
        PetFullResponseDTO response = PetTestConstants.GET_PET_RESPONSE_DTO;

        // Arrange
        when(petService.getPet(
                eq(1),
                any(Authentication.class)
        )).thenReturn(response);

        // Act
        MvcResult result = mockMvc.perform(get("/api/users/pets/1"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        verify(petService).getPet(
                eq(1),
                any(Authentication.class)
        );

        // Print Response Debug Statements
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_OWNER")
    void getPet_WhenNoPetIDExists_ShouldThrowPetNotFoundException() throws Exception
    {
        // Arrange
        String request = PetTestConstants.STRING_REGISTRATION_DTO;

        // Act

        // Assert

    }
}
