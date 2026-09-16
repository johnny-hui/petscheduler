package com.johnny.petscheduler.entities.pet;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PetDetails
{
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

    @Column(name = "is_vaccinated")
    private Boolean isVaccinated;

    @Column(name = "has_bite_history")
    private Boolean hasBiteHistory;

    @Column(name = "can_be_left_alone")
    private Boolean canBeLeftAlone;

    @Column(name = "profile_desc", length = 1000)
    private String profileDescription;

    @Column(name = "extra_notes", length = 1000)
    private String extraNotes;
}
