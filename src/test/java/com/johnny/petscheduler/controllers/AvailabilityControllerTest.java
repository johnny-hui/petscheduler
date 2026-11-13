package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.security.configs.SecurityConfig;
import com.johnny.petscheduler.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;

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

    // Mock other SecurityConfig dependencies as needed,
    // such as your JWT filter or authentication entry point.

    private AvailabilityRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );
    }
}
