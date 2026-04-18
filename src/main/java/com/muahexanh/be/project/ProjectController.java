package com.muahexanh.be.project;

import com.muahexanh.be.project.dto.ApplyProjectRequest;
import com.muahexanh.be.project.dto.BanStudentRequest;
import com.muahexanh.be.project.dto.CreateProjectRequest;
import com.muahexanh.be.project.dto.ProjectApplicationResponse;
import com.muahexanh.be.project.dto.ProjectMemberResponse;
import com.muahexanh.be.project.dto.ProjectResponse;
import com.muahexanh.be.project.dto.UpdateProjectApplicationStatusRequest;
import com.muahexanh.be.project.dto.UpdateProjectRequest;
import com.muahexanh.be.user.User;
import com.muahexanh.be.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        User currentUser = getCurrentUser();
        Project project = projectService.createProject(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.fromEntity(project));
    }

    @PostMapping("/applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProjectApplicationResponse> applyToProject(@Valid @RequestBody ApplyProjectRequest request) {
        User currentUser = getCurrentUser();
        ProjectApplication application = projectService.applyToProject(request.getProjectId(), currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectApplicationResponse.fromEntity(application));
    }

    @GetMapping("/applications/pending")
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<List<ProjectApplicationResponse>> getPendingApplications() {
        List<ProjectApplicationResponse> applications = projectService.getPendingApplications()
                .stream()
                .map(ProjectApplicationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<ProjectApplicationResponse> reviewApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateProjectApplicationStatusRequest request) {
        User currentUser = getCurrentUser();
        ProjectApplication application = projectService.reviewApplication(
                applicationId,
                request.getStatus(),
                currentUser.getId());
        return ResponseEntity.ok(ProjectApplicationResponse.fromEntity(application));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects()
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(ProjectResponse.fromEntity(project));
    }

    @GetMapping("/leader/{leaderId}")
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getProjectsByLeader(@PathVariable Long leaderId) {
        List<ProjectResponse> projects = projectService.getProjectsByLeader(leaderId)
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('UNI_ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        User currentUser = getCurrentUser();
        Project project = projectService.updateProject(id, request, currentUser.getId());
        return ResponseEntity.ok(ProjectResponse.fromEntity(project));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<List<ProjectMemberResponse>> getAcceptedStudentsByProject(@PathVariable Long id) {
        List<ProjectMemberResponse> students = projectService.getAcceptedApplicationsByProject(id)
                .stream()
                .map(ProjectMemberResponse::fromApplication)
                .toList();
        return ResponseEntity.ok(students);
    }

    @PatchMapping("/{id}/students/ban")
    @PreAuthorize("hasAnyRole('COMMUNITY_LEADER','UNI_ADMIN')")
    public ResponseEntity<ProjectApplicationResponse> banStudentFromProject(
            @PathVariable Long id,
            @Valid @RequestBody BanStudentRequest request) {
        User currentUser = getCurrentUser();
        ProjectApplication application = projectService.banStudentFromProject(id, request.getStudentId(),
                currentUser.getId());
        return ResponseEntity.ok(ProjectApplicationResponse.fromEntity(application));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
    }
}