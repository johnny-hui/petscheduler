package com.johnny.petscheduler.DTO.registration;

import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class RegisterPetBusinessDTO extends RegisterUserDTO {
    @Length(max = 50, message = "Business name cannot exceed 50 characters")
    @NotBlank(message = "A business name must be provided")
    private String businessName;

    @Length(max = 50, message = "Address cannot exceed 50 characters")
    @NotBlank(message = "A business address must be provided")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    @NotBlank(message = "A City must be provided")
    private String city;

    @NotNull(message = "Establishment year is required")
    @Positive(message = "Establishment year cannot be negative")
    private Integer establishmentYear;

    @Nullable
    @Size(max = 100, message = "Profile title cannot exceed 100 characters")
    private String profileTitle;

    @Nullable
    @Length(max = 1000, message = "Your profile description must not exceed 1000 characters")
    private String profileDescription;

    @Nullable
    @URL(message = "An invalid URL was provided")
    private String websiteURL;

    @Nullable
    @Pattern(
            regexp = RegexUtil.PHONE_NUMBER,
            message = "Invalid phone number was provided!"
    )
    private String phoneNumber;
}
