package com.johnny.petscheduler.entities;

import com.johnny.petscheduler.entities.booking.Booking;
import com.johnny.petscheduler.entities.pet.Pet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "booking_pet",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_pet",
                        columnNames = {"booking_id", "pet_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_booking_pet_pet",
                        columnList = "pet_id"
                )
        }
)
@NoArgsConstructor
@Getter @Setter
public class PetBooking
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
