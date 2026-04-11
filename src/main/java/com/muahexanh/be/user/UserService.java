package com.muahexanh.be.user;

import com.muahexanh.be.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found with id: " + userId));

        return mapToUserProfileResponse(user, profile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        UserProfile profile = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User profile not found"));

        return mapToUserProfileResponse(user, profile);
    }

    private UserProfileResponse mapToUserProfileResponse(User user, UserProfile profile) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getAbilitiesDescription(),
                profile.getOrganizationName(),
                user.getRole(),
                user.getStatus()
        );
    }

}
