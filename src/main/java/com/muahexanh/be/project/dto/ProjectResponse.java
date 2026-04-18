package com.muahexanh.be.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.muahexanh.be.project.Project;
import com.muahexanh.be.project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String requiredSkills;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime endTime;
    
    private Integer amountOfParticipants;
    private ProjectStatus status;
    private Long leaderId;
    private String leaderName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getRequiredSkills(),
                project.getStartTime(),
                project.getEndTime(),
                project.getAmountOfParticipants(),
                project.getStatus(),
                project.getLeader().getId(),
                project.getLeader().getFullName(),
                project.getCreatedAt()
        );
    }
}
