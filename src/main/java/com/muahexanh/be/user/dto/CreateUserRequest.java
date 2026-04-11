package com.muahexanh.be.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Full name must not be blank")
        @Size(max = 100, message = "Full name must be at most 100 characters")
        String fullName
) {
}

