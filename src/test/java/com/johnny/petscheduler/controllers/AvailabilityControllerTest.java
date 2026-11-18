package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.exceptions.availability.AvailabilityNotFoundException;
import com.johnny.petscheduler.security.configs.SecurityConfig;
import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import com.johnny.petscheduler.security.user.CustomUserDetailsService;
import com.johnny.petscheduler.security.utils.JWTUtil;
import com.johnny.petscheduler.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    // Create Tests
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

    // GET tests
    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getAvailability_ExistingID_Returns200() throws Exception
    {
        Long availabilityID = 1L;

        when(availabilityService.getAvailability(availabilityID))
                .thenReturn(expectedResponse);

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailability(availabilityID);

        printResponse(result);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getAvailability_InvalidID_Returns400(Long availabilityID) throws Exception
    {
        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .getAvailability(anyLong());

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getAvailability_NotFound_Returns404() throws Exception
    {
        Long availabilityID = 999L;
        when(availabilityService.getAvailability(availabilityID))
                .thenThrow(
                        new AvailabilityNotFoundException("Availability not found")
                );

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isNotFound())
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailability(availabilityID);

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getOwnAvailabilities_AuthenticatedUser_Returns200() throws Exception
    {
        when(availabilityService.getAvailabilities(
                isNull(),
                any(Authentication.class)
        )).thenReturn(List.of(expectedResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/me")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailabilities(
                        isNull(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getUserAvailabilities_ValidUserID_Returns200() throws Exception
    {
        Long userID = 5L;

        when(availabilityService.getAvailabilities(
                eq(userID),
                isNull()
        )).thenReturn(List.of(expectedResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/user/{userID}", userID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailabilities(
                        eq(userID),
                        isNull()
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getUserAvailabilities_InvalidUserID_Returns400() throws Exception
    {
        Long userID = -1L;

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/user/{userID}", userID)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .getAvailabilities(
                        anyLong(),
                        any()
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getPreviousOwnAvailabilities_AuthenticatedUser_Returns200() throws Exception
    {
        AvailabilityResponseDTO previousResponse = new AvailabilityResponseDTO(
                3L,
                LocalDate.of(2026, 10, 15),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0)
        );

        when(availabilityService.getPreviousAvailabilities(
                isNull(),
                any(Authentication.class)
        )).thenReturn(List.of(previousResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/me/history")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].availableDate").value("2026-10-15"))
                .andExpect(jsonPath("$[0].startTime").value("10:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("13:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getPreviousAvailabilities(
                        isNull(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void getPreviousUserAvailabilities_ValidUserID_Returns200() throws Exception
    {
        Long userID = 5L;

        AvailabilityResponseDTO previousResponse = new AvailabilityResponseDTO(
                3L,
                LocalDate.of(2026, 10, 15),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0)
        );

        when(availabilityService.getPreviousAvailabilities(
                eq(userID),
                isNull()
        )).thenReturn(List.of(previousResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/user/{userID}/history", userID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].availableDate").value("2026-10-15"))
                .andExpect(jsonPath("$[0].startTime").value("10:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("13:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getPreviousAvailabilities(
                        eq(userID),
                        isNull()
                );

        printResponse(result);
    }

    // Query (search) Availability Tests
    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void searchAvailabilities_ValidQuery_Returns200() throws Exception
    {
        LocalDate date = LocalDate.of(2026, 12, 15);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        when(availabilityService.getAvailabilitiesByQuery(
                eq(date),
                eq(startTime),
                eq(endTime),
                eq(1),
                eq(10),
                any(Authentication.class)
        )).thenReturn(List.of(expectedResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/search")
                                .param("date", "2026-12-15")
                                .param("startTime", "09:00")
                                .param("endTime", "12:00")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailabilitiesByQuery(
                        eq(date),
                        eq(startTime),
                        eq(endTime),
                        eq(1),
                        eq(10),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void searchAvailabilities_MissingRequiredParameter_Returns400() throws Exception
    {
        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/search")
                                .param("startTime", "09:00")
                                .param("endTime", "12:00")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(availabilityService);
        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void searchAvailabilities_InvalidDateTimeFormat_Returns400() throws Exception
    {
        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/search")
                                .param("date", "invalid-date")
                                .param("startTime", "invalid-time")
                                .param("endTime", "12:00")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(availabilityService);
        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void searchAvailabilities_DefaultPagination_Returns200() throws Exception
    {
        LocalDate date = LocalDate.of(2026, 12, 15);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        when(availabilityService.getAvailabilitiesByQuery(
                eq(date),
                eq(startTime),
                eq(endTime),
                eq(0),
                eq(20),
                any(Authentication.class)
        )).thenReturn(List.of(expectedResponse));

        MvcResult result = mockMvc.perform(
                        get(BASE_URL + "/search")
                                .param("date", "2026-12-15")
                                .param("startTime", "09:00")
                                .param("endTime", "12:00")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andReturn();

        verify(availabilityService, times(1))
                .getAvailabilitiesByQuery(
                        eq(date),
                        eq(startTime),
                        eq(endTime),
                        eq(0),
                        eq(20),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    // Update Tests
    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void updateAvailability_ValidRequest_Returns200() throws Exception
    {
        Long availabilityID = 1L;

        when(availabilityService.updateAvailability(
                eq(availabilityID),
                eq(validRequest),
                any(Authentication.class)
        )).thenReturn(expectedResponse);

        MvcResult result = mockMvc.perform(
                        put(BASE_URL + "/{availabilityID}", availabilityID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.availableDate").value("2026-12-15"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("12:00:00"))
                .andReturn();

        verify(availabilityService, times(1))
                .updateAvailability(
                        eq(availabilityID),
                        eq(validRequest),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void updateAvailability_InvalidDTO_Returns400() throws Exception
    {
        Long availabilityID = 1L;

        AvailabilityRequestDTO invalidRequest = new AvailabilityRequestDTO(
                null,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        MvcResult result = mockMvc.perform(
                        put(BASE_URL + "/{availabilityID}", availabilityID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .updateAvailability(
                        anyLong(),
                        any(AvailabilityRequestDTO.class),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void updateAvailability_InvalidID_Returns400() throws Exception
    {
        Long availabilityID = -1L;

        MvcResult result = mockMvc.perform(
                        put(BASE_URL + "/{availabilityID}", availabilityID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .updateAvailability(
                        anyLong(),
                        any(AvailabilityRequestDTO.class),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    // Delete Tests
    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void deleteAvailability_Success_Returns204() throws Exception
    {
        Long availabilityID = 1L;

        when(availabilityService.deleteAvailability(
                eq(availabilityID),
                any(Authentication.class)
        )).thenReturn(
                ResponseEntity.noContent().build()
        );

        MvcResult result = mockMvc.perform(
                        delete(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andReturn();

        verify(availabilityService, times(1))
                .deleteAvailability(
                        eq(availabilityID),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void deleteAvailability_InvalidID_Returns400() throws Exception
    {
        Long availabilityID = -1L;

        MvcResult result = mockMvc.perform(
                        delete(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(availabilityService, never())
                .deleteAvailability(
                        anyLong(),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "PET_BUSINESS")
    void deleteAvailability_NotFound_Returns404() throws Exception
    {
        Long availabilityID = 999L;

        when(availabilityService.deleteAvailability(
                eq(availabilityID),
                any(Authentication.class)
        )).thenThrow(
                new AvailabilityNotFoundException("Availability not found")
        );

        MvcResult result = mockMvc.perform(
                        delete(BASE_URL + "/{availabilityID}", availabilityID)
                )
                .andExpect(status().isNotFound())
                .andReturn();

        verify(availabilityService, times(1))
                .deleteAvailability(
                        eq(availabilityID),
                        any(Authentication.class)
                );

        printResponse(result);
    }

    private void printResponse(final MvcResult result) throws Exception
    {
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}
