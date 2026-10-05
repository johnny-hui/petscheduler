package com.johnny.petscheduler.DTO.user.update;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePetSitterDTO extends UpdateUserDTO {
    @Nullable
    @Size(max = 100, message = "Profile title cannot exceed 200 characters")
    private String profileTitle;

    @Nullable
    @Length(max = 1000, message = "Your profile description must not exceed 1000 characters")
    private String profileDescription;
}
