package com.muahexanh.be.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRegistrationApplicationRepository extends JpaRepository<StudentRegistrationApplication, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<StudentRegistrationApplication> findByStatus(RegistrationStatus status);
}
