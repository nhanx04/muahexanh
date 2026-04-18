package com.muahexanh.be.project;

import com.muahexanh.be.project.dto.CreateProjectRequest;
import com.muahexanh.be.user.User;
import com.muahexanh.be.user.UserRepository;
import com.muahexanh.be.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    @Transactional
    public Project createProject(CreateProjectRequest request, Long leaderId) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("Leader not found"));

        Project project = new Project();
        project.setLeader(leader);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setRequiredSkills(request.getRequiredSkills());
        project.setStartTime(convertToLocalDateTime(request.getStartTime()));
        project.setEndTime(convertToLocalDateTime(request.getEndTime()));
        project.setAmountOfParticipants(request.getAmountOfParticipants());
        project.setStatus(ProjectStatus.PENDING);

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByLeader(Long leaderId) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("Leader not found"));
        if (leader.getRole() != UserRole.COMMUNITY_LEADER && leader.getRole() != UserRole.UNI_ADMIN) {
            throw new IllegalArgumentException("User is not a leader or admin");
        }
        return projectRepository.findByLeaderId(leaderId);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Transactional
    public ProjectApplication applyToProject(Long projectId, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Only STUDENT can apply to a project");
        }

        Project project = getProjectById(projectId);

        boolean alreadyApplied = projectApplicationRepository.existsByProjectIdAndStudentId(projectId, studentId);
        if (alreadyApplied) {
            throw new IllegalArgumentException("You have already applied to this project");
        }

        ProjectApplication application = new ProjectApplication();
        application.setProject(project);
        application.setStudent(student);
        application.setStatus(ProjectApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());
        return projectApplicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ProjectApplication> getPendingApplications() {
        return projectApplicationRepository.findByStatus(ProjectApplicationStatus.APPLIED);
    }

    @Transactional
    public ProjectApplication reviewApplication(Long applicationId, ProjectApplicationStatus status, Long reviewerId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
        if (reviewer.getRole() != UserRole.COMMUNITY_LEADER && reviewer.getRole() != UserRole.UNI_ADMIN) {
            throw new IllegalArgumentException("Only COMMUNITY_LEADER or UNI_ADMIN can review applications");
        }

        ProjectApplication application = projectApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Project application not found"));

        if (application.getStatus() != ProjectApplicationStatus.APPLIED) {
            throw new IllegalArgumentException("This application has already been reviewed");
        }
        if (status == ProjectApplicationStatus.APPLIED || status == ProjectApplicationStatus.BANNED) {
            throw new IllegalArgumentException("Review status must be ACCEPTED or REJECTED");
        }

        if (status == ProjectApplicationStatus.ACCEPTED) {
            long acceptedCount = projectApplicationRepository.countByProjectIdAndStatus(
                    application.getProject().getId(), ProjectApplicationStatus.ACCEPTED);
            if (acceptedCount >= application.getProject().getAmountOfParticipants()) {
                throw new IllegalArgumentException("Project has reached the participant limit");
            }
        }

        application.setStatus(status);
        return projectApplicationRepository.save(application);
    }

    @Transactional
    public ProjectApplication banStudentFromProject(Long projectId, Long studentId, Long reviewerId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
        if (reviewer.getRole() != UserRole.COMMUNITY_LEADER && reviewer.getRole() != UserRole.UNI_ADMIN) {
            throw new IllegalArgumentException("Only COMMUNITY_LEADER or UNI_ADMIN can ban students");
        }

        getProjectById(projectId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Target user is not a STUDENT");
        }

        ProjectApplication application = projectApplicationRepository
                .findByProjectIdAndStatus(projectId, ProjectApplicationStatus.ACCEPTED)
                .stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Student is not an accepted member of this project"));

        application.setStatus(ProjectApplicationStatus.BANNED);
        return projectApplicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ProjectApplication> getAcceptedApplicationsByProject(Long projectId) {
        getProjectById(projectId);
        return projectApplicationRepository.findByProjectIdAndStatus(projectId, ProjectApplicationStatus.ACCEPTED);
    }

    @Transactional(readOnly = true)
    public List<ProjectApplication> getMyApplications(Long studentId) {
        return projectApplicationRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<ProjectApplication> getApplicationByUserAndProject(Long projectId, Long studentId) {
        return projectApplicationRepository.findByProjectIdAndStudentId(projectId, studentId);
    }

    private static java.time.LocalDateTime convertToLocalDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDateTime();
    }
}