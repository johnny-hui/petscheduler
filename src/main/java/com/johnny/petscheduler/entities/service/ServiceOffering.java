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
@NoArgsConstructor
@Getter @Setter
public class ServiceOffering
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Can be a PetSitter or PetBusiness
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User serviceProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_rate", nullable = false)
    private ServiceRate serviceRate;

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

    // For fixed-rate services only (PER_WALK, PER_SESSION)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // For PER_HOUR services only
    @Column(name = "min_duration_minutes")
    private Integer minDurationMinutes;

    // For PER_HOUR services only
    @Column(name = "max_duration_minutes")
    private Integer maxDurationMinutes;

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
}
