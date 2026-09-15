package com.johnny.petscheduler.entities.pet;

import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A Pet entity.
 *
 * @implNote Relationships:
 *      One-to-Many [Pet -> PetTrait]
 *      Many-to-One [Pet -> User]
 */
@Entity
@Table(name = "pets")
@Getter @Setter @NoArgsConstructor
public final class Pet
{
    // TODO: Enforce an upper limit to the amount of Pets a user can have (max. 10)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    @Size(max = 50)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "breed")
    private String breed;

    @Column(name = "is_vaccinated")
    private boolean isVaccinated;

    @Column(name = "has_bite_history")
    private boolean hasBiteHistory;

    @Column(name = "can_be_left_alone")
    private boolean canBeLeftAlone;

    @Column(name = "profile_desc")
    private String profileDescription;

    @Column(name = "extra_notes")
    private String extraNotes;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PetType type;

    @Column(name = "size", nullable = false)
    @Enumerated(EnumType.STRING)
    private PetSize size;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private PetGender gender;

    @Column(name = "energy_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnergyLevel energyLevel;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetTraitAssignment> traits = new ArrayList<>();
}
