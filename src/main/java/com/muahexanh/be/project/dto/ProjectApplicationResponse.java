package com.muahexanh.be.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.muahexanh.be.project.ProjectApplication;
import com.muahexanh.be.project.ProjectApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProjectApplicationResponse {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long studentId;
    private String studentName;
    private ProjectApplicationStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime appliedAt;

    public static ProjectApplicationResponse fromEntity(ProjectApplication application) {
        return new ProjectApplicationResponse(
                application.getId(),
                application.getProject().getId(),
                application.getProject().getTitle(),
                application.getStudent().getId(),
                application.getStudent().getFullName(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
