package com.johnny.petscheduler.entities.service;

import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "service_offering",
        indexes = {
                @Index(
                        name = "idx_service_offering_provider",
                        columnList = "provider_id"
                )
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter @Setter
public abstract class ServiceOffering
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User serviceProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    @Column(
            name = "base_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal basePrice;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(
            name = "additional_price_per_pet",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal additionalPricePerPet = BigDecimal.ZERO;

    @Column(name = "description", length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    /**
     * Determines the combined price of the ServiceOffering by
     * adding the base price by the price per additional pet.
     *
     * @param petCount
     *      An integer for the pet count
     *
     * @return <code>Combined Price</code>
     *      The combined price (base price + [price per pet x (petCount - 1)])
     */
    protected BigDecimal calculateCombinedPrice(final int petCount)
    {
        if (petCount < 1) {
            throw new IllegalArgumentException(
                    "At least one pet is required."
            );
        }

        return basePrice.add(
                additionalPricePerPet.multiply(
                        BigDecimal.valueOf(petCount - 1)
                )
        );
    }

    public abstract ServiceRate getServiceRate();

    public abstract BigDecimal calculatePrice(
            int petCount,
            long requestedDurationMinutes
    );

}
