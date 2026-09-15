package com.johnny.petscheduler.DTO.registration;

import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class RegisterPetOwnerDTO extends RegisterUserDTO
{
    @Nullable
    @Length(max = 100, message = "Your bio cannot exceed 100 characters")
    private String bio;
}
