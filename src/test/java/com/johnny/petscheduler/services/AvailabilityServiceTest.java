package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DAO.BookingDAO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.AvailabilityBookingConflictException;
import com.johnny.petscheduler.exceptions.availability.AvailabilityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest
{
    @Mock
    private AvailabilityDAO availabilityDAO;

    @Mock
    private BookingDAO bookingDAO;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Availability availability;
    private User serviceProvider;

    @BeforeEach
    void setUp() {
        serviceProvider = new PetSitter();
        serviceProvider.setId(1L);

        availability = new Availability();
        availability.setId(10L);
        availability.setServiceProvider(serviceProvider);
        availability.setAvailableDate(LocalDate.of(2026, 12, 10));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
    }

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
}