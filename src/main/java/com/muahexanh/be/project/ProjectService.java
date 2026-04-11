package com.muahexanh.be.project;

import com.muahexanh.be.project.dto.CreateProjectRequest;
import com.muahexanh.be.user.User;
import com.muahexanh.be.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(CreateProjectRequest request, Long leaderId) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

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

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private static java.time.LocalDateTime convertToLocalDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDateTime();
    }
}