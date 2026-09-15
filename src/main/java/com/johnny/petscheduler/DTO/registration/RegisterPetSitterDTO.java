package com.johnny.petscheduler.DTO.registration;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class RegisterPetSitterDTO extends RegisterUserDTO
{
    @NotNull(message = "Years of experience is required")
    @Positive(message = "Years of experience cannot be a negative value")
    private Integer yearsOfExperience;

    @Nullable
    @Size(max = 100, message = "Profile title cannot exceed 200 characters")
    private String profileTitle;

    @Nullable
    @Length(max = 1000, message = "Your profile description must not exceed 1000 characters")
    private String profileDescription;
}
