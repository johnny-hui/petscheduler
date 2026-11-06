package com.johnny.petscheduler.DTO.registration;

import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

/**
 * A DTO that binds base user information for the registration of a User.
 */
@Getter @Setter
@NoArgsConstructor
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

        @NotBlank(message = "A country is required")
        @Pattern(
                regexp = RegexUtil.COUNTRY_CODE,
                message = "A Country must be a two-letter ISO country code (e.g. CA, US, JP)"
        )
        private String country;

        @NotBlank(message = "A city is required")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        @Pattern(
                regexp = RegexUtil.CITY_PATTERN,
                message = "City contains invalid characters"
        )
        private String city;

        @NotBlank(message = "A timezone is required")
        private String timezone;

        @Nullable
        @URL(message = "An invalid URL was provided")
        private String avatarURL;
}
