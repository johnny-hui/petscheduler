package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.security.configs.SecurityConfig;
import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import com.johnny.petscheduler.security.user.CustomUserDetailsService;
import com.johnny.petscheduler.security.utils.JWTUtil;
import com.johnny.petscheduler.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
@Import(SecurityConfig.class)
class AvailabilityControllerTest
{
    private static final String BASE_URL = "/api/availabilities";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AvailabilityService availabilityService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPointJWT authEntryPointJWT;

    private AvailabilityRequestDTO validRequest;
    private AvailabilityResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        expectedResponse = new AvailabilityResponseDTO(
                1L,
                LocalDate.of(2026, 12, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createAvailability_ValidRequest_Returns201() throws Exception
    {
        when(availabilityService.createAvailability(
                eq(validRequest),
                any(Authentication.class)
        )).thenReturn(expectedResponse);

        MvcResult result = mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .createAvailability(
                        eq(validRequest),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createAvailability_InvalidRequest_Returns400() throws Exception
    {
        AvailabilityRequestDTO invalidRequest = new AvailabilityRequestDTO(
                null,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        MvcResult result = mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .createAvailability(
                        any(AvailabilityRequestDTO.class),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createBulkAvailabilities_ValidList_Returns201() throws Exception
    {
        AvailabilityRequestDTO secondRequest = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 16),
                LocalTime.of(13, 0),
                LocalTime.of(16, 0)
        );

        AvailabilityResponseDTO secondResponse = new AvailabilityResponseDTO(
                2L,
                LocalDate.of(2026, 12, 16),
                LocalTime.of(13, 0),
                LocalTime.of(16, 0)
        );

        List<AvailabilityRequestDTO> requests = List.of(
                validRequest,
                secondRequest
        );

        List<AvailabilityResponseDTO> responses = List.of(
                expectedResponse,
                secondResponse
        );

        when(availabilityService.createBulkAvailabilities(
                eq(requests),
                any(Authentication.class)
        )).thenReturn(responses);

        MvcResult result = mockMvc.perform(
                        post(BASE_URL + "/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("12:00:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].availableDate").value("2026-12-16"))
                .andExpect(jsonPath("$[1].startTime").value("13:00:00"))
                .andExpect(jsonPath("$[1].endTime").value("16:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .createBulkAvailabilities(
                        eq(requests),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createBulkAvailabilities_EmptyList_Returns400() throws Exception
    {
        MvcResult result = mockMvc.perform(
                        post(BASE_URL + "/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("[]")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .createBulkAvailabilities(
                        anyList(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createBulkAvailabilities_MoreThanFive_Returns400() throws Exception
    {
        List<AvailabilityRequestDTO> requests = IntStream.range(0, 6)
                .mapToObj(i -> new AvailabilityRequestDTO(
                        LocalDate.of(2026, 12, 15).plusDays(i),
                        LocalTime.of(9, 0),
                        LocalTime.of(12, 0)
                ))
                .toList();

        MvcResult result = mockMvc.perform(
                        post(BASE_URL + "/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .createBulkAvailabilities(
                        anyList(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void createBulkAvailabilities_OneInvalidDTO_Returns400() throws Exception
    {
        AvailabilityRequestDTO invalidRequest = new AvailabilityRequestDTO(
                null,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        List<AvailabilityRequestDTO> requests = List.of(
                validRequest,
                invalidRequest
        );

        MvcResult result = mockMvc.perform(
                        post(BASE_URL + "/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .createBulkAvailabilities(
                        anyList(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    private void printResponse(MvcResult result) throws Exception
    {
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}
