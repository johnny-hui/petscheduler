package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DAO.BookingDAO;
import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.InvalidUserRoleException;
import com.johnny.petscheduler.exceptions.UserNotFoundException;
import com.johnny.petscheduler.exceptions.availability.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import java.util.Collections;
import java.util.List;
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
    private User user;

    @BeforeEach
    void setUp() {
        setTestClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );

        user = new PetSitter();  // default test class
        user.setId(1L);
        user.setTimezone(TEST_TIMEZONE);

        availability = new Availability();
        availability.setId(10L);
        availability.setServiceProvider(user);
        availability.setAvailableDate(LocalDate.of(2026, 12, 10));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
    }

    // GET Tests
    @Test
    void getAvailability_ExistingId_ReturnsResponseDTO() {
        // Arrange
        when(availabilityDAO.getAvailabilityById(10L))
                .thenReturn(Optional.of(availability));

        // Act
        AvailabilityResponseDTO result =
                availabilityService.getAvailability(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(LocalDate.of(2026, 12, 10), result.availableDate());
        assertEquals(LocalTime.of(9, 0), result.startTime());
        assertEquals(LocalTime.of(17, 0), result.endTime());
        verify(availabilityDAO).getAvailabilityById(10L);
    }

    @Test
    void getAvailability_NonExistingId_ThrowsNotFoundException() {
        // Arrange
        when(availabilityDAO.getAvailabilityById(10L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                AvailabilityNotFoundException.class,
                () -> availabilityService.getAvailability(10L)
        );
        verify(availabilityDAO).getAvailabilityById(10L);
    }

    @Test
    void getAvailabilities_AuthenticatedPetSitter_ReturnsOwnAvailabilities() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        )).thenReturn(List.of(availability));

        // Act
        List<AvailabilityResponseDTO> result =
                availabilityService.getAvailabilities(null, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().id());
        verifyNoInteractions(userDAO);
        verify(availabilityDAO).getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );
    }

    @Test
    void getAvailabilities_AuthenticatedPetBusiness_ReturnsOwnAvailabilities() {
        // Arrange
        user = new PetBusiness();
        user.setId(1L);
        user.setTimezone(TEST_TIMEZONE);
        availability.setServiceProvider(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        )).thenReturn(List.of(availability));

        // Act
        List<AvailabilityResponseDTO> result =
                availabilityService.getAvailabilities(
                        null, authentication
                );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().id());
        verifyNoInteractions(userDAO);
        verify(availabilityDAO).getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );
    }

    @Test
    void getAvailabilities_AuthenticatedPetOwner_ThrowsInvalidUserRoleException() {
        // Arrange
        user = new PetOwner();
        user.setId(1L);
        user.setTimezone(TEST_TIMEZONE);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act & Assert
        assertThrows(
                InvalidUserRoleException.class,
                () -> availabilityService.getAvailabilities(
                        null, authentication
                )
        );
        verifyNoInteractions(userDAO, availabilityDAO);
    }

    @Test
    void getAvailabilities_ValidPublicRequest_ReturnsProviderAvailabilities() {
        // Arrange
        when(userDAO.getUserById(1L))
                .thenReturn(Optional.of(user));

        when(availabilityDAO.getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        )).thenReturn(List.of(availability));

        // Act
        List<AvailabilityResponseDTO> result =
                availabilityService.getAvailabilities(1L, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().id());
        verify(userDAO).getUserById(1L);
        verify(availabilityDAO).getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );
    }

    @Test
    void getAvailabilities_PublicRequest_UserNotFound_ThrowsException() {
        // Arrange
        when(userDAO.getUserById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> availabilityService.getAvailabilities(1L, null)
        );
        verify(userDAO).getUserById(1L);
        verifyNoInteractions(availabilityDAO);
    }

    @Test
    void getAvailabilities_PublicRequest_PetOwner_ThrowsInvalidUserRoleException() {
        // Arrange
        user = new PetOwner();
        user.setId(1L);
        user.setTimezone(TEST_TIMEZONE);

        when(userDAO.getUserById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(
                InvalidUserRoleException.class,
                () -> availabilityService.getAvailabilities(1L, null)
        );
        verify(userDAO).getUserById(1L);
        verifyNoInteractions(availabilityDAO);
    }

    @Test
    void getAvailabilities_NoUpcomingAvailabilities_ReturnsEmptyList() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        )).thenReturn(Collections.emptyList());

        // Act
        List<AvailabilityResponseDTO> result =
                availabilityService.getAvailabilities(
                        null, authentication
                );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(availabilityDAO).getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );
    }

    @Test
    void getAvailabilities_UserInDifferentTimezone_UsesLocalDateTime() {
        // Arrange
        setTestClock(
                "America/Vancouver",  // December 1 at 10:00 in Vancouver
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );

        // Same instant is December 2 at 03:00 in Tokyo
        user.setTimezone("Asia/Tokyo");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 2),
                LocalTime.of(3, 0)
        )).thenReturn(Collections.emptyList());

        // Act
        List<AvailabilityResponseDTO> result =
                availabilityService.getAvailabilities(
                        null, authentication
                );

        // Assert
        assertTrue(result.isEmpty());
        verify(availabilityDAO).getUpcomingAvailabilities(
                1L,
                LocalDate.of(2026, 12, 2),
                LocalTime.of(3, 0)
        );
    }

    @Nested
    class GetPreviousAvailabilitiesTests {
        @Test
        void getPreviousAvailabilities_AuthenticatedPetSitter_ReturnsPastAvailabilities() {
            // Arrange
            availability.setAvailableDate(LocalDate.of(2026, 11, 30));
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal())
                    .thenReturn(user);

            when(availabilityDAO.getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            )).thenReturn(List.of(availability));

            // Act
            List<AvailabilityResponseDTO> result =
                    availabilityService.getPreviousAvailabilities(
                            null, authentication
                    );

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.getFirst().id());
            assertEquals(
                    LocalDate.of(2026, 11, 30),
                    result.getFirst().availableDate()
            );
            verifyNoInteractions(userDAO);
            verify(availabilityDAO).getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            );
            verify(availabilityDAO, never())
                    .getUpcomingAvailabilities(
                            anyLong(), any(), any()
                    );
        }

        @Test
        void getPreviousAvailabilities_AuthenticatedPetBusiness_ReturnsPastAvailabilities() {
            // Arrange
            user = new PetBusiness();
            user.setId(1L);
            user.setTimezone(TEST_TIMEZONE);

            availability.setServiceProvider(user);
            availability.setAvailableDate(LocalDate.of(2026, 11, 30));

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal())
                    .thenReturn(user);

            when(availabilityDAO.getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            )).thenReturn(List.of(availability));

            // Act
            List<AvailabilityResponseDTO> result =
                    availabilityService.getPreviousAvailabilities(
                            null, authentication
                    );

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.getFirst().id());
            verifyNoInteractions(userDAO);
            verify(availabilityDAO).getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            );
        }

        @Test
        void getPreviousAvailabilities_AuthenticatedPetOwner_ThrowsInvalidUserRoleException() {
            // Arrange
            user = new PetOwner();
            user.setId(1L);
            user.setTimezone(TEST_TIMEZONE);

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal())
                    .thenReturn(user);

            // Act & Assert
            assertThrows(
                    InvalidUserRoleException.class,
                    () -> availabilityService.getPreviousAvailabilities(
                            null, authentication
                    )
            );
            verifyNoInteractions(userDAO, availabilityDAO);
        }

        @Test
        void getPreviousAvailabilities_PublicRequest_ExistingProvider_ReturnsPastAvailabilities() {
            // Arrange
            availability.setAvailableDate(LocalDate.of(2026, 11, 30));

            when(userDAO.getUserById(1L))
                    .thenReturn(Optional.of(user));

            when(availabilityDAO.getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            )).thenReturn(List.of(availability));

            // Act
            List<AvailabilityResponseDTO> result =
                    availabilityService.getPreviousAvailabilities(1L, null);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.getFirst().id());
            verify(userDAO).getUserById(1L);
            verify(availabilityDAO).getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            );
        }

        @Test
        void getPreviousAvailabilities_PublicRequest_UserNotFound_ThrowsException() {
            // Arrange
            when(userDAO.getUserById(1L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UserNotFoundException.class,
                    () -> availabilityService.getPreviousAvailabilities(1L, null)
            );
            verify(userDAO).getUserById(1L);
            verifyNoInteractions(availabilityDAO);
        }

        @Test
        void getPreviousAvailabilities_PublicRequest_PetOwner_ThrowsInvalidUserRoleException() {
            // Arrange
            user = new PetOwner();
            user.setId(1L);
            user.setTimezone(TEST_TIMEZONE);

            when(userDAO.getUserById(1L))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            assertThrows(
                    InvalidUserRoleException.class,
                    () -> availabilityService.getPreviousAvailabilities(1L, null)
            );
            verify(userDAO).getUserById(1L);
            verifyNoInteractions(availabilityDAO);
        }

        @Test
        void getPreviousAvailabilities_NoPastAvailabilities_ReturnsEmptyList() {
            // Arrange
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal())
                    .thenReturn(user);

            when(availabilityDAO.getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            )).thenReturn(Collections.emptyList());

            // Act
            List<AvailabilityResponseDTO> result =
                    availabilityService.getPreviousAvailabilities(
                            null, authentication
                    );

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(availabilityDAO).getPastAvailabilities(
                    1L,
                    LocalDate.of(2026, 12, 1),
                    LocalTime.of(10, 0)
            );
        }
    }

    // Create Tests
    @Test
    void createAvailability_whenValidAsAPetSitter_returnsResponseDTO() {
        // Arrange
        user = new PetSitter();
        user.setId(1L);
        user.setTimezone(TEST_TIMEZONE);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        AvailabilityRequestDTO request = createValidRequest();

        when(availabilityDAO.isAvailabilityOverlapping(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        // Act
        AvailabilityResponseDTO response =
                availabilityService.createAvailability(
                        request,
                        authentication
                );

        // Assert
        assertNotNull(response);
        verify(availabilityDAO, times(1))
                .create(any(Availability.class));
    }

    @Test
    void createAvailability_WhenValidAsAPetBusiness_returnsResponseDTO() {
        // Arrange
        user = new PetBusiness();
        user.setId(2L);
        user.setTimezone(TEST_TIMEZONE);

        AvailabilityRequestDTO request = createValidRequest();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.isAvailabilityOverlapping(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        // Act
        AvailabilityResponseDTO response =
                availabilityService.createAvailability(
                        request,
                        authentication
                );

        // Assert
        assertNotNull(response);
        verify(availabilityDAO, times(1))
                .create(any(Availability.class));
    }

    @Test
    void createAvailability_petOwner_throwsAccessDeniedException() {
        // Arrange
        user = new PetOwner();
        user.setId(3L);
        user.setTimezone(TEST_TIMEZONE);

        AvailabilityRequestDTO request = createValidRequest();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act + Assert
        assertThrows(
                AccessDeniedException.class,
                () -> availabilityService.createAvailability(
                        request,
                        authentication
                )
        );
        verifyNoInteractions(availabilityDAO);
    }

    @Test
    void createAvailability_overlappingWindow_throwsException() {
        // Arrange
        AvailabilityRequestDTO request = createValidRequest();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.isAvailabilityOverlapping(
                any(), any(), any(), any(), any()
        )).thenReturn(true);

        // Act + Assert
        assertThrows(
                AvailabilityOverlapException.class,
                () -> availabilityService.createAvailability(
                        request,
                        authentication
                )
        );

        verify(availabilityDAO, never())
                .create(any(Availability.class));
    }

    @Test
    void createAvailability_startTimeAfterEndTime_shouldThrowException() {
        // Arrange
        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                LocalTime.of(11, 0),  // start time
                LocalTime.of(5, 0)    // end time
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act + Assert
        assertThrows(
                InvalidAvailabilityTimeException.class,
                () -> availabilityService.createAvailability(
                        request,
                        authentication
                )
        );
        verifyNoInteractions(availabilityDAO);
    }

    @Test
    void createBulkAvailabilities_validRequests_returnsAllResponses() {
        // Arrange
        AvailabilityRequestDTO request1 = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        AvailabilityRequestDTO request2 = new AvailabilityRequestDTO(
                availability.getAvailableDate().plusDays(1),
                availability.getStartTime(),
                availability.getEndTime()
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.isAvailabilityOverlapping(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        // Act
        List<AvailabilityResponseDTO> responses =
                availabilityService.createBulkAvailabilities(
                        List.of(request1, request2),
                        authentication
                );

        // Assert
        assertEquals(2, responses.size());
        assertEquals(
                request1.availableDate(),
                responses.get(0).availableDate()
        );
        assertEquals(
                request2.availableDate(),
                responses.get(1).availableDate()
        );
        verify(availabilityDAO, times(2))
                .isAvailabilityOverlapping(any(), any(), any(), any(), any());
        verify(availabilityDAO, times(2))
                .create(any(Availability.class));
    }

    @Test
    void createBulkAvailabilities_oneOverlap_throwsException() {
        // Arrange
        AvailabilityRequestDTO request1 = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        AvailabilityRequestDTO request2 = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        when(availabilityDAO.isAvailabilityOverlapping(
                any(), any(), any(), any(), any()
        )).thenReturn(false, true);

        // Act + Assert
        assertThrows(
                AvailabilityOverlapException.class,
                () -> availabilityService.createBulkAvailabilities(
                        List.of(request1, request2),
                        authentication
                )
        );
        verify(availabilityDAO, times(1))
                .create(any(Availability.class));
    }

    @Test
    void createBulkAvailabilities_petOwner_throwsAccessDeniedException() {
        // Arrange
        user = new PetOwner();
        user.setId(2L);
        user.setTimezone(TEST_TIMEZONE);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act + Assert
        assertThrows(
                AccessDeniedException.class,
                () -> availabilityService.createBulkAvailabilities(
                        List.of(request),
                        authentication
                )
        );
        verifyNoInteractions(availabilityDAO);
    }

    // Update Tests
    @Test
    void updateAvailability_whenValid_shouldUpdate()
    {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
        )).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                user.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        )).thenReturn(false);

        when(availabilityDAO.isAvailabilityOverlapping(
                availability.getId(),
                user.getId(),
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 11),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
        )).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                user.getId(),
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.of(2026, 12, 14),
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                requestedDate,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                requestedDate,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                today,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L, user.getId()
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
                .thenReturn(user);

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                availability.getId(),
                availability.getServiceProvider().getId())
        ).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                user.getId(),
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
                .thenReturn(user);

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
                .thenReturn(user);

        when(availabilityDAO.getAvailabilityByIdAndProviderId(
                10L,
                availability.getServiceProvider().getId())
        ).thenReturn(Optional.of(availability));

        when(bookingDAO.anyActiveBookings(
                user.getId(),
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


    private AvailabilityRequestDTO createValidRequest()
    {
        return new AvailabilityRequestDTO(
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );
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