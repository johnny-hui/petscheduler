package com.johnny.petscheduler.DTO.user.update;

import com.johnny.petscheduler.entities.users.BusinessType;
import com.johnny.petscheduler.utils.RegexUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePetBusinessDTO extends UpdateUserDTO {
    @Nullable
    @Length(max = 50, message = "Address cannot exceed 50 characters")
    private String address;

    @Nullable
    private BusinessType businessType;

    @Nullable
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

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
