package com.muahexanh.be.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
    boolean existsByProjectIdAndStudentId(Long projectId, Long studentId);

    long countByProjectIdAndStatus(Long projectId, ProjectApplicationStatus status);

    List<ProjectApplication> findByStatus(ProjectApplicationStatus status);

    List<ProjectApplication> findByProjectIdAndStatus(Long projectId, ProjectApplicationStatus status);

    List<ProjectApplication> findByStudentId(Long studentId);

    Optional<ProjectApplication> findByProjectIdAndStudentId(Long projectId, Long studentId);
}