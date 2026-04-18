package com.muahexanh.be.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BanStudentRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;
}

