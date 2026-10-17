package com.johnny.petscheduler.DTO.user.update;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePetOwnerDTO extends UpdateUserDTO {
    @Nullable
    @Length(max = 100, message = "Your bio cannot exceed 100 characters")
    private String bio;
}
