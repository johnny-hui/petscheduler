package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.entities.service.ServiceOffering;
import com.johnny.petscheduler.entities.service.ServiceRate;
import com.johnny.petscheduler.exceptions.booking.InvalidBookingDurationException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Contains utility methods that helps perform various operations
 * for a Booking entity.
 */
public class BookingUtil
{
    private BookingUtil(){}

    /**
     * Calculates the total price of the Booking based on the
     * type of service offered.
     *
     * @param service
     *      A ServiceOffering object
     *
     * @param petCount
     *      An integer for the pet count
     *
     * @return <code>unitPrice</code>
     */
    public static BigDecimal calculateUnitPrice(final ServiceOffering service,
                                                final int petCount)
    {
        if (petCount < 1) {
            throw new IllegalArgumentException(
                    "Booking must contain at least one pet"
            );
        }

        BigDecimal additionalPetCost =
                service.getAdditionalPricePerPet()
                        .multiply(BigDecimal.valueOf(petCount - 1));

        return service.getBasePrice()
                .add(additionalPetCost);
    }

    /**
     * Validates whether a Booking has a valid duration. This is to prevent users
     * from submitting time durations that do not align with ServiceOffering.
     *
     * @param service
     *      A ServiceOffering object
     *
     * @param startTime
     *      A LocalTime object
     *
     * @param endTime
     *      A LocalTime object
     */
    public static void validateBookingDuration(final ServiceOffering service,
                                               final LocalTime startTime,
                                               final LocalTime endTime)
    {
        long bookingMinutes = Duration
                .between(startTime, endTime)
                .toMinutes();

        // Validate 15-min time increment
        if (bookingMinutes <= 0 || bookingMinutes % 15 != 0) {
            throw new InvalidBookingDurationException(
                    "Booking duration must be positive and in 15-minute increments"
            );
        }

        // Validate flexible or fixed-rate booking times
        if (service.getServiceRate() == ServiceRate.PER_HOUR) // Flexible-rate
        {
            if (bookingMinutes < service.getMinDurationMinutes()
                    || bookingMinutes > service.getMaxDurationMinutes())
            {
                throw new InvalidBookingDurationException(
                        "Booking duration is outside the allowed range"
                );
            }
        } else {
            if (bookingMinutes != service.getDurationMinutes()) {  // Fixed-rate (specific)
                throw new InvalidBookingDurationException(
                        "Booking duration must match the service duration"
                );
            }
        }
    }
}
