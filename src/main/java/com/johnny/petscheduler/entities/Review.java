package com.johnny.petscheduler.entities;

import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor
public class Review
{
    public static final String MIN_RATING = "1.0";
    public static final String MAX_RATING = "5.0";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewer_id",  nullable = false)
    private PetOwner reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;

    @Column(name = "rating")
    @NotNull(message = "The review must include a rating!")
    @DecimalMin(value = MIN_RATING, message = "Review ratings cannot be lower than 1.0")
    @DecimalMax(value = MAX_RATING, message = "Review ratings cannot be higher than 5.0")
    private Double rating;

    @Column(name = "author")
    @NotBlank(message = "The review must include an author!")
    private String author;

    @Column(name = "comment")
    @NotBlank(message = "The review must include a comment!")
    @Size(min = 10, max = 500)
    private String comment;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
}
