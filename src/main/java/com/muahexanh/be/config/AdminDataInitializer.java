package com.muahexanh.be.config;

import com.muahexanh.be.user.User;
import com.muahexanh.be.user.UserProfile;
import com.muahexanh.be.user.UserProfileRepository;
import com.muahexanh.be.user.UserRepository;
import com.muahexanh.be.user.UserRole;
import com.muahexanh.be.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String adminUsername = "admin";

        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail("admin@muahexanh.local");
        admin.setFullName("System Admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.UNI_ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(admin);

        String adminEmail = "admin@muahexanh.local";
        if (userProfileRepository.findByEmail(adminEmail).isEmpty()) {
            UserProfile profile = new UserProfile();
            profile.setUser(saved);
            profile.setFullName("System Admin");
            profile.setEmail(adminEmail);
            profile.setOrganizationName("Mua He Xanh");
            userProfileRepository.save(profile);
        }

        log.warn(
                "Seeded default admin account: username='admin', password='admin123'. Please change it after first login.");
    }
}
