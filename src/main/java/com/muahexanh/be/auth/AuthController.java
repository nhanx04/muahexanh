package com.muahexanh.be.auth;

import com.muahexanh.be.auth.dto.CreateUserByAdminRequest;
import com.muahexanh.be.auth.dto.LoginRequest;
import com.muahexanh.be.auth.dto.LoginResponse;
import com.muahexanh.be.auth.dto.StudentRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", authService.createUserByAdmin(request)));
    }

}
