package com.muahexanh.be.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        String fullName,

        @Email(message = "Email should be valid")
        String email,

        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phoneNumber,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        @Size(max = 1000, message = "Abilities description must not exceed 1000 characters")
        String abilitiesDescription,

        @Size(max = 255, message = "Organization name must not exceed 255 characters")
        String organizationName
) {
}
