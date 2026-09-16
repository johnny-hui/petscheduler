package com.johnny.petscheduler.entities.pet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A bridging class (entity) that associates a Pet with a specific PetTrait (enum).
 *
 * @implSpec Relationships:
 *      Many-to-One [PetTrait -> Pet]
 */
@Entity
@Table(name = "pet_traits")
@Getter @Setter @NoArgsConstructor
public final class PetTraitAssignment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "trait", nullable = false)
    @Enumerated(EnumType.STRING)
    private PetTrait trait;
}
