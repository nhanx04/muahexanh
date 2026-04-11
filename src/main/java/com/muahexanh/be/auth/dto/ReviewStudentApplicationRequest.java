package com.muahexanh.be.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewStudentApplicationRequest(
        @NotNull Boolean approved,
        @Size(max = 1000) String reviewerNote
) {
}

