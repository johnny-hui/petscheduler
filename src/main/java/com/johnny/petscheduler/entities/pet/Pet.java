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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "breed", length = 100)
    private String breed;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Embedded
    private PetDetails details;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetTraitAssignment> traits = new ArrayList<>();
}
