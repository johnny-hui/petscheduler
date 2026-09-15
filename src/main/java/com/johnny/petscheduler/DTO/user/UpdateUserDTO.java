package com.johnny.petscheduler.DTO.user;

import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter @Setter @NoArgsConstructor
public abstract class UpdateUserDTO
{
    @Nullable
    @Email(message = "A valid email must be provided")
    private String email;

    @Nullable
    @Length(min = 6, message = "Password must be minimum 6 characters or more")
    @Pattern(
            regexp = RegexUtil.PASSWORD_CHECK,
            message = "Password must contain an uppercase letter, lowercase letter, number, and special character"
    )
    private String password;

    @Nullable
    @Pattern(
            regexp = RegexUtil.CANADIAN_POSTAL_CODE,
            message = "Invalid Postal Code was provided"
    )
    private String postalCode;

    @Nullable
    @URL(message = "An invalid URL was provided")
    private String avatarURL;
}
