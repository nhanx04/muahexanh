package com.muahexanh.be.auth;

import com.muahexanh.be.auth.dto.CreateUserByAdminRequest;
import com.muahexanh.be.auth.dto.LoginRequest;
import com.muahexanh.be.auth.dto.LoginResponse;
import com.muahexanh.be.auth.dto.ReviewStudentApplicationRequest;
import com.muahexanh.be.auth.dto.StudentApplicationResponse;
import com.muahexanh.be.auth.dto.StudentRegisterRequest;
import com.muahexanh.be.user.User;
import com.muahexanh.be.user.UserProfile;
import com.muahexanh.be.user.UserProfileRepository;
import com.muahexanh.be.user.UserRepository;
import com.muahexanh.be.user.UserRole;
import com.muahexanh.be.user.UserStatus;
import com.muahexanh.be.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StudentRegistrationApplicationRepository appRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public String registerStudent(StudentRegisterRequest request) {
        ensureUsernameAndEmailAvailable(request.username(), request.email());

        StudentRegistrationApplication app = new StudentRegistrationApplication();
        app.setUsername(request.username().trim());
        app.setPassword(passwordEncoder.encode(request.password()));
        app.setFullName(request.fullName().trim());
        app.setEmail(request.email().trim().toLowerCase());
        app.setPhoneNumber(request.phoneNumber());
        app.setAddress(request.address());
        app.setAbilitiesDescription(request.abilitiesDescription());
        app.setStatus(RegistrationStatus.PENDING);

        appRepository.save(app);
        return "Đã gửi đơn đăng ký, chờ duyệt";
    }

    @Transactional
    public String createUserByAdmin(CreateUserByAdminRequest request) {
        if (request.role() == UserRole.STUDENT) {
            throw new IllegalArgumentException("API này chỉ tạo COMMUNITY_LEADER hoặc UNI_ADMIN");
        }
        ensureUsernameAndEmailAvailable(request.username(), request.email());

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setFullName(request.fullName().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(saved);
        profile.setFullName(request.fullName().trim());
        profile.setEmail(request.email().trim().toLowerCase());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setAddress(request.address());
        profile.setOrganizationName(request.organizationName());
        userProfileRepository.save(profile);

        return "Tạo tài khoản thành công";
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Sai thông tin đăng nhập"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản chưa active hoặc đã bị khóa");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", user.getId(), user.getUsername(), user.getRole());
    }

    @Transactional(readOnly = true)
    public List<StudentApplicationResponse> getPendingApplications() {
        return appRepository.findByStatus(RegistrationStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StudentApplicationResponse reviewStudentApplication(Long applicationId,
            ReviewStudentApplicationRequest request) {
        StudentRegistrationApplication app = appRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn"));

        if (app.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalArgumentException("Đơn đã được xử lý");
        }

        User reviewer = getCurrentUser();
        app.setReviewedBy(reviewer);
        app.setReviewerNote(request.reviewerNote());

        if (request.approved()) {
            ensureUsernameAndEmailAvailable(app.getUsername(), app.getEmail());
            User student = new User();
            student.setUsername(app.getUsername());
            student.setEmail(app.getEmail());
            student.setFullName(app.getFullName());
            student.setPassword(app.getPassword());
            student.setRole(UserRole.STUDENT);
            student.setStatus(UserStatus.ACTIVE);
            User saved = userRepository.save(student);

            UserProfile profile = new UserProfile();
            profile.setUser(saved);
            profile.setFullName(app.getFullName());
            profile.setEmail(app.getEmail());
            profile.setPhoneNumber(app.getPhoneNumber());
            profile.setAddress(app.getAddress());
            profile.setAbilitiesDescription(app.getAbilitiesDescription());
            userProfileRepository.save(profile);

            app.setStatus(RegistrationStatus.APPROVED);
        } else {
            app.setStatus(RegistrationStatus.REJECTED);
        }

        return toResponse(appRepository.save(app));
    }

    private void ensureUsernameAndEmailAvailable(String username, String email) {
        if (userRepository.findByUsername(username.trim()).isPresent()
                || appRepository.existsByUsername(username.trim())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()
                || appRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user hiện tại"));
    }

    private StudentApplicationResponse toResponse(StudentRegistrationApplication app) {
        return new StudentApplicationResponse(
                app.getId(),
                app.getUsername(),
                app.getFullName(),
                app.getEmail(),
                app.getStatus(),
                app.getReviewerNote(),
                app.getReviewedBy() != null ? app.getReviewedBy().getId() : null,
                app.getCreatedAt(),
                app.getUpdatedAt());
    }
}
