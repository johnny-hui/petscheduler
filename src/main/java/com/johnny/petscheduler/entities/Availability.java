package com.johnny.petscheduler.entities;

import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "availability",
        indexes = {
                @Index(
                        name = "idx_availability_sitter_date",
                        columnList = "sitter_id, available_date"
                )
        }
)
@NoArgsConstructor
@Getter @Setter
public class Availability
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOTE: A serviceProvider can be either a PetSitter or PetBusiness
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User serviceProvider;

    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Version // to prevent DB conflicts
    private Long version;
}
