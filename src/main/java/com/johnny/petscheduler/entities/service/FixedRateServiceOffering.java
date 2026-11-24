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
     * @param requestedDurationMinutes
     *      A long for the requested duration in minutes
     *
     * @return <code>Total Price</code>
     *      The total price of the ServiceOffering
     */
    @Override
    public BigDecimal calculatePrice(final int petCount, final long requestedDurationMinutes)
    {
        if (requestedDurationMinutes != this.durationMinutes) {
            throw new IllegalArgumentException(
                    "Invalid fixed service duration."
            );
        }
        return this.calculateCombinedPrice(petCount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
