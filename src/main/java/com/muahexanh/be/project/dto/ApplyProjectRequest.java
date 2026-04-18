package com.muahexanh.be.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyProjectRequest {
    @NotNull(message = "Project ID is required")
    private Long projectId;
}
