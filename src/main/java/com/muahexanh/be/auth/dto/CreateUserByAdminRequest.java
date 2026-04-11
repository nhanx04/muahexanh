package com.muahexanh.be.auth.dto;

import com.muahexanh.be.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserByAdminRequest(
        @NotBlank @Size(min = 4, max = 100) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotNull UserRole role,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String phoneNumber,
        String address,
        String organizationName
) {
}

