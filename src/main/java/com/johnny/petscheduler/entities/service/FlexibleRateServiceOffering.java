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
        if (requestedDurationMinutes < minDurationMinutes
                || requestedDurationMinutes > maxDurationMinutes)
        {
            throw new IllegalArgumentException(
                    "Duration is outside the permitted range."
            );
        }

        return this.calculateCombinedPrice(petCount)
                .multiply(BigDecimal.valueOf(requestedDurationMinutes))
                .divide(
                        BigDecimal.valueOf(60),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}