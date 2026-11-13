package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DAO.BookingDAO;
import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.AvailabilityBookingConflictException;
import com.johnny.petscheduler.exceptions.availability.AvailabilityNotFoundException;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityDateException;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest
{
    private static final String TEST_TIMEZONE = "America/Vancouver";

    @Mock
    private AvailabilityDAO availabilityDAO;

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Availability availability;
    private User serviceProvider;

    @BeforeEach
    void setUp() {
        setTestClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );

        serviceProvider = new PetSitter();
        serviceProvider.setId(1L);
        serviceProvider.setTimezone(TEST_TIMEZONE);

        availability = new Availability();
        availability.setId(10L);
        availability.setServiceProvider(serviceProvider);
        availability.setAvailableDate(LocalDate.of(2026, 12, 10));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
    }

    // Update Tests
    @Test
    void updateAvailability_whenValid_shouldUpdate()
    {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                serviceProvider.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        )).thenReturn(false);

        when(availabilityDAO.isAvailabilityOverlapping(
                availability.getId(),
                serviceProvider.getId(),
                request.availableDate(),
                request.startTime(),
                request.endTime()
        )).thenReturn(false);

        // Act
        AvailabilityResponseDTO response = availabilityService.updateAvailability(
                10L,
                request,
                authentication
        );

        // Assert
        assertNotNull(response);
        assertEquals(
                request.availableDate(),
                availability.getAvailableDate()
        );
        assertEquals(
                request.startTime(),
                availability.getStartTime()
        );
        assertEquals(
                request.endTime(),
                availability.getEndTime()
        );
    }

    @Test
    void updateAvailability_whenNotFound_shouldThrow() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                AvailabilityNotFoundException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );
        verifyNoInteractions(bookingDAO);
    }

    @Test
    void updateAvailability_whenPetOwner_shouldThrow() {
        // Arrange
        User petOwner = new PetOwner();
        petOwner.setId(2L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(petOwner);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );
        verifyNoInteractions(availabilityDAO, bookingDAO);
    }

    @Test
    void updateAvailability_whenActiveBooking_shouldThrow() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                serviceProvider.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        )).thenReturn(true);

        // Act & Assert
        assertThrows(
                AvailabilityBookingConflictException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );

        // Assert original values remain unchanged
        assertEquals(
                LocalDate.of(2026, 12, 10),
                availability.getAvailableDate()
        );
        assertEquals(
                LocalTime.of(9, 0),
                availability.getStartTime()
        );
        assertEquals(
                LocalTime.of(17, 0),
                availability.getEndTime()
        );
    }

    @Test
    void updateAvailability_whenNothingChanged_shouldReturnExisting() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        // Act
        AvailabilityResponseDTO response =
                availabilityService.updateAvailability(
                        10L, request, authentication
                );

        // Assert
        assertNotNull(response);
        assertEquals(
                availability.getAvailableDate(),
                response.availableDate()
        );
        verifyNoInteractions(bookingDAO);
    }

    @Test
    void updateAvailability_whenPastDate_shouldThrow() {
        // Arrange
        setTestClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 15),
                LocalTime.of(10, 0)
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 14),
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        // Act & Assert
        assertThrows(
                InvalidAvailabilityDateException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );
        verifyNoInteractions(bookingDAO);
    }

    @Test
    void updateAvailability_whenBeyond90Days_shouldThrow() {
        // Arrange
        LocalDate today = LocalDate.of(2026, 12, 1);
        setTestClock(
                TEST_TIMEZONE,
                today,
                LocalTime.of(10, 0)
        );
        LocalDate requestedDate = today.plusDays(91);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                requestedDate,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        // Act & Assert
        assertThrows(
                InvalidAvailabilityDateException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );
        verifyNoInteractions(bookingDAO);
    }

    @ParameterizedTest
    @CsvSource({
            "0, true",
            "89, true",
            "90, true",
            "91, false"
    })
    void updateAvailability_whenChecking90DayLimit(int daysAhead, boolean shouldBeValid)
    {
        // Arrange
        LocalDate today = LocalDate.of(2026, 12, 1);
        setTestClock(
                TEST_TIMEZONE,
                today,
                LocalTime.of(8, 0)
        );
        LocalDate requestedDate = today.plusDays(daysAhead);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                requestedDate,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        // Act + Assert
        if(shouldBeValid) {
            AvailabilityResponseDTO response =
                    availabilityService.updateAvailability(
                            10L, request, authentication
                    );
            assertNotNull(response);
            assertEquals(
                    request.availableDate(),
                    availability.getAvailableDate()
            );
            assertEquals(
                    request.startTime(),
                    availability.getStartTime()
            );
            assertEquals(
                    request.endTime(),
                    availability.getEndTime()
            );
        } else {
            assertThrows(
                    InvalidAvailabilityDateException.class,
                    () -> availabilityService.updateAvailability(
                            10L, request, authentication
                    )
            );
            verifyNoInteractions(bookingDAO);
        }
    }

    @Test
    void updateAvailability_whenTodayStartTimePassed_shouldThrow()
    {
        // Arrange
        LocalDate today = LocalDate.of(2026, 12, 1);
        setTestClock(
                TEST_TIMEZONE,
                today,
                LocalTime.of(14, 0)
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                today,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, serviceProvider.getId()
        )).thenReturn(Optional.of(availability));

        // Act & Assert
        assertThrows(
                InvalidAvailabilityTimeException.class,
                () -> availabilityService.updateAvailability(
                        10L, request, authentication
                )
        );
        verifyNoInteractions(bookingDAO);
    }

    // Delete Tests
    @Test
    void deleteAvailability_whenValid_shouldDelete() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                availability.getId(),
                availability.getServiceProvider().getId())
        ).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                serviceProvider.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime())
        ).thenReturn(false);

        // Act
        availabilityService.deleteAvailability(10L, authentication);

        // Assert
        verify(availabilityDAO).delete(availability);
    }

    @Test
    void deleteAvailability_whenNotFound_shouldThrow() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L,
                availability.getServiceProvider().getId())
        ).thenReturn(Optional.empty());

        // Act
        assertThrows(
                AvailabilityNotFoundException.class,
                () -> availabilityService.deleteAvailability(10L, authentication)
        );

        // Assert
        verify(availabilityDAO, never()).delete(any(Availability.class));
    }

    @Test
    void deleteAvailability_whenPetOwner_shouldThrow() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        User user = new PetOwner();
        user.setId(1L);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> availabilityService.deleteAvailability(
                        10L, authentication)
        );

        // Verify no database operations occurred
        verifyNoInteractions(availabilityDAO, bookingDAO);
    }

    @Test
    void deleteAvailability_whenActiveBooking_shouldThrow() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(serviceProvider);

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L,
                availability.getServiceProvider().getId())
        ).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                serviceProvider.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime())
        ).thenReturn(true);

        // Act
        assertThrows(
                AvailabilityBookingConflictException.class,
                () -> availabilityService.deleteAvailability(10L, authentication)
        );

        // Assert
        verify(availabilityDAO, never())
                .delete(any(Availability.class));
    }

    private Clock createFixedClock(final String timezone,
                                   final LocalDate date,
                                   final LocalTime time)
    {
        ZoneId zone = ZoneId.of(timezone);
        return Clock.fixed(
                LocalDateTime.of(date, time)
                        .atZone(zone)
                        .toInstant(),
                zone
        );
    }

    private void setTestClock(final String timezone,
                              final LocalDate date,
                              final LocalTime time)
    {
        Clock clock = createFixedClock(timezone, date, time);

        availabilityService = new AvailabilityService(
                availabilityDAO,
                bookingDAO,
                userDAO,
                clock
        );
    }
}