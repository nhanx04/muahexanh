package com.muahexanh.be.auth;

import com.muahexanh.be.auth.dto.CreateUserByAdminRequest;
import com.muahexanh.be.auth.dto.LoginRequest;
import com.muahexanh.be.auth.dto.LoginResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public String registerStudent(StudentRegisterRequest request) {
        ensureUsernameAndEmailAvailable(request.username(), request.email());

        User student = new User();
        student.setUsername(request.username().trim());
        student.setEmail(request.email().trim().toLowerCase());
        student.setFullName(request.fullName().trim());
        student.setPassword(passwordEncoder.encode(request.password()));
        student.setRole(UserRole.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(student);

        UserProfile profile = new UserProfile();
        profile.setUser(saved);
        profile.setFullName(request.fullName().trim());
        profile.setEmail(request.email().trim().toLowerCase());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setAddress(request.address());
        profile.setAbilitiesDescription(request.abilitiesDescription());
        userProfileRepository.save(profile);

        return "Student account created successfully";
    }

    @Transactional
    public String createUserByAdmin(CreateUserByAdminRequest request) {
        if (request.role() == UserRole.STUDENT) {
            throw new IllegalArgumentException("This API can only create COMMUNITY_LEADER or UNI_ADMIN accounts");
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

        return "Account created successfully";
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active or has been locked");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", user.getId(), user.getUsername(), user.getRole());
    }

    private void ensureUsernameAndEmailAvailable(String username, String email) {
        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
