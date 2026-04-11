package com.muahexanh.be.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRegisterRequest(
        @NotBlank @Size(min = 4, max = 100) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String phoneNumber,
        String address,
        String abilitiesDescription
) {
}

