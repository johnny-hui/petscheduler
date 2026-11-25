package com.johnny.petscheduler.entities.service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;

@Entity
@Table(name = "flexible_rate_service_offering")
@PrimaryKeyJoinColumn(name = "service_id")
@NoArgsConstructor
@Getter @Setter
public class FlexibleRateServiceOffering extends ServiceOffering
{
    @NotNull
    @Positive
    @Column(name = "min_duration_minutes", nullable = false)
    private Integer minDurationMinutes;

    @NotNull
    @Positive
    @Column(name = "max_duration_minutes", nullable = false)
    private Integer maxDurationMinutes;

    @Override
    public ServiceRate getServiceRate() {
        return ServiceRate.PER_HOUR;
    }

    /**
     * A utility method that calculates the price of a flexible-rate (per-hour)
     * ServiceOffering, which can be used to calculate the total price a Booking.
     *
     * @param petCount
     *      An integer for the number of pets
     *
     * @param requestedDurationMinutes
     *      A long for the requested duration in minutes
     *
     * @return <code>Total Price</code>
     *      The total price of the ServiceOffering
     */
    @Override
    public BigDecimal calculatePrice(final int petCount, final long requestedDurationMinutes)
    {
        validateBookingDuration(requestedDurationMinutes);
        return this.calculateCombinedPrice(petCount)
                .multiply(BigDecimal.valueOf(requestedDurationMinutes))
                .divide(
                        BigDecimal.valueOf(60),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    /**
     * Validates and determines the endTime for a FlexibleRateServiceOffering (per-hour)
     * based on the duration of the Booking (requestedStartTime && requestedEndTime)
     *
     * @param requestedStartTime
     *      A LocalTime object
     *
     * @param requestedEndTime
     *      A LocalTime object (not used)
     *
     * @return <code>endTime</code>
     *      The validated endTime
     */
    @Override
    public LocalTime resolveEndTime(final LocalTime requestedStartTime,
                                    final LocalTime requestedEndTime)
    {
        if (requestedEndTime == null) {
            throw new IllegalArgumentException(
                    "An end time is required for flexible-rate services."
            );
        }

        if (!requestedStartTime.isBefore(requestedEndTime)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time."
            );
        }

        long durationMinutes = Duration
                .between(requestedStartTime, requestedEndTime)
                .toMinutes();

        validateBookingDuration(durationMinutes);
        return requestedEndTime;
    }

    /**
     * Validates and checks whether a Booking's duration is valid for a
     * FlexibleRateServiceOffering.
     *
     * @param requestedDurationMinutes
     *      A Long for the requested duration (in minutes)
     */
    public void validateBookingDuration(final long requestedDurationMinutes)
    {
        if (requestedDurationMinutes <= 0 ||
                requestedDurationMinutes % 15 != 0 ||
                requestedDurationMinutes < minDurationMinutes ||
                requestedDurationMinutes > maxDurationMinutes)
        {
            throw new IllegalArgumentException(
                    "Booking duration is outside the permitted range."
            );
        }
    }
}