package com.johnny.petscheduler.entities.pet;

import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * Represented in JSON equivalent: "YYYY-MM-DD" or "2000-05-15"
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "breed", length = 100)
    private String breed;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Embedded
    private PetDetails details;

    @ElementCollection
    @CollectionTable(
            name = "pet_traits",
            joinColumns = @JoinColumn(name = "pet_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<PetTrait> traits = new HashSet<>();
}
