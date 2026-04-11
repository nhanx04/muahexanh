package com.muahexanh.be.auth;

import com.muahexanh.be.auth.dto.CreateUserByAdminRequest;
import com.muahexanh.be.auth.dto.LoginRequest;
import com.muahexanh.be.auth.dto.LoginResponse;
import com.muahexanh.be.auth.dto.ReviewStudentApplicationRequest;
import com.muahexanh.be.auth.dto.StudentApplicationResponse;
import com.muahexanh.be.auth.dto.StudentRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/student")
    public ResponseEntity<Map<String, String>> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", authService.registerStudent(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/create-user")
    @PreAuthorize("hasRole('UNI_ADMIN')")
    public ResponseEntity<Map<String, String>> createUserByAdmin(@Valid @RequestBody CreateUserByAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", authService.createUserByAdmin(request)));
    }

    @GetMapping("/applications/pending")
    @PreAuthorize("hasAnyRole('UNI_ADMIN','COMMUNITY_LEADER')")
    public ResponseEntity<List<StudentApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(authService.getPendingApplications());
    }

    @PostMapping("/applications/{id}/review")
    @PreAuthorize("hasAnyRole('UNI_ADMIN','COMMUNITY_LEADER')")
    public ResponseEntity<StudentApplicationResponse> review(
            @PathVariable Long id,
            @Valid @RequestBody ReviewStudentApplicationRequest request) {
        return ResponseEntity.ok(authService.reviewStudentApplication(id, request));
    }
}

