package com.muahexanh.be.project.dto;

import com.muahexanh.be.project.ProjectApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectApplicationStatusRequest {
    @NotNull(message = "Status is required")
    private ProjectApplicationStatus status;
}
