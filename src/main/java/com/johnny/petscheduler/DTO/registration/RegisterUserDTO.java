package com.johnny.petscheduler.DTO.registration;

import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

/**
 * A DTO that binds information for the registration of a User.
 */
@Getter @Setter @NoArgsConstructor
public abstract class RegisterUserDTO
{
        @Length(max = 30, message = "First name field cannot exceed 30 characters")
        @NotBlank(message = "First name field must be filled")
        private String firstName;

        @Length(max = 30, message="Last name field cannot exceed 30 characters")
        @NotBlank(message = "Last name field must be filled")
        private String lastName;

        @Email(message = "A valid email must be provided")
        @NotBlank(message = "A valid email must be provided")
        private String email;

        @Length(min = 6, message = "Password must be minimum 6 characters or more")
        @NotBlank(message = "A password must be provided")
        @Pattern(
                regexp = RegexUtil.PASSWORD_CHECK,
                message = "Password must contain an uppercase letter, lowercase letter, number, and special character"
        )
        private String password;

        @Pattern(
                regexp = RegexUtil.CANADIAN_POSTAL_CODE,
                message = "Invalid Postal Code was provided"
        )
        @NotBlank(message = "A postal code must be provided")
        private String postalCode;

        @Nullable
        @URL(message = "An invalid URL was provided")
        private String avatarURL;
}
