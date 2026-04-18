package com.muahexanh.be.project.dto;

import com.muahexanh.be.project.ProjectApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectMemberResponse {
    private Long studentId;
    private String username;
    private String fullName;
    private String email;

    public static ProjectMemberResponse fromApplication(ProjectApplication application) {
        return new ProjectMemberResponse(
                application.getStudent().getId(),
                application.getStudent().getUsername(),
                application.getStudent().getFullName(),
                application.getStudent().getEmail());
    }
}

