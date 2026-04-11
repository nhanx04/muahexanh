package com.muahexanh.be.auth.dto;

import com.muahexanh.be.auth.RegistrationStatus;

import java.time.LocalDateTime;

public record StudentApplicationResponse(
        Long id,
        String username,
        String fullName,
        String email,
        RegistrationStatus status,
        String reviewerNote,
        Long reviewedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

