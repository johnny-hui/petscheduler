package com.johnny.petscheduler.entities.service;

import com.johnny.petscheduler.exceptions.InvalidServiceRateException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

@Entity
@Table(name = "fixed_rate_service_offering")
@PrimaryKeyJoinColumn(name = "service_id")
@NoArgsConstructor
@Getter @Setter
public class FixedRateServiceOffering extends ServiceOffering
{
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_rate", nullable = false)
    private ServiceRate serviceRate;  // => PER_SESSION or PER_WALK only

    @NotNull
    @Positive
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /**
     * Validates and sets a fixed ServiceRate.
     *
     * @param serviceRate
     *      A ServiceRate enum
     */
    public void setServiceRate(final ServiceRate serviceRate)
    {
        if (serviceRate == null || !serviceRate.isFixedRate()) {
            throw new InvalidServiceRateException(
                    "A fixed-rate service must be either PER_WALK or PER_SESSION."
            );
        }
        this.serviceRate = serviceRate;
    }

    /**
     * A utility method that calculates the price of a fixed-rate (per-session)
     * ServiceOffering, which can be used to calculate the total price a Booking.
     *
     * @param petCount
     *      An integer for the number of pets
     *
     * @param durationMinutes
     *      A long for the duration in minutes (not used)
     *
     * @return <code>Total Price</code>
     *      The total price of the ServiceOffering
     */
    @Override
    public BigDecimal calculatePrice(final int petCount,
                                     final long durationMinutes)
    {
        return this.calculateCombinedPrice(petCount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates and returns a new endTime for a Booking object from
     * the FixedRateServiceOffering's duration.
     *
     * @param startTime
     *      A LocalTime object
     *
     * @param requestedEndTime
     *      A LocalTime object (not used)
     *
     * @return <code>endTime</code>
     *      The calculated and set endTime
     */
    @Override
    public LocalTime resolveEndTime(final LocalTime startTime,
                                    final LocalTime requestedEndTime)
    {
        // Create and set a new endTime for the Booking
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        // Prevent midnight rollover
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Overnight bookings are not supported.");
        }
        return endTime;
    }
}
