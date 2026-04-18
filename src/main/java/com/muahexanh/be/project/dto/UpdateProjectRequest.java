package com.muahexanh.be.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.muahexanh.be.project.ProjectStatus;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class UpdateProjectRequest {
    private String title;

    private String description;

    private String requiredSkills;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime endTime;

    @Positive(message = "Amount of participants must be positive")
    private Integer amountOfParticipants;

    private ProjectStatus status;
}
