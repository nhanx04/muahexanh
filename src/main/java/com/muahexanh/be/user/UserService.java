package com.muahexanh.be.user;

import com.muahexanh.be.user.dto.UpdateUserProfileRequest;
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

    @Transactional
    public UserProfileResponse updateMyProfile(UpdateUserProfileRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        UserProfile profile = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User profile not found"));

        // Update User entity (email, fullName)
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }

        // Update UserProfile entity
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            profile.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null && !request.address().isBlank()) {
            profile.setAddress(request.address());
        }
        if (request.abilitiesDescription() != null && !request.abilitiesDescription().isBlank()) {
            profile.setAbilitiesDescription(request.abilitiesDescription());
        }
        if (request.organizationName() != null && !request.organizationName().isBlank()) {
            profile.setOrganizationName(request.organizationName());
        }

        userRepository.save(user);
        userProfileRepository.save(profile);

        return mapToUserProfileResponse(user, profile);
    }

    @Transactional
    public UserProfileResponse partialUpdateMyProfile(UpdateUserProfileRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        UserProfile profile = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User profile not found"));

        // Partial update - only update fields that are provided and not null/blank
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            profile.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null && !request.address().isBlank()) {
            profile.setAddress(request.address());
        }
        if (request.abilitiesDescription() != null) {
            profile.setAbilitiesDescription(request.abilitiesDescription());
        }
        if (request.organizationName() != null) {
            profile.setOrganizationName(request.organizationName());
        }

        userRepository.save(user);
        userProfileRepository.save(profile);

        return mapToUserProfileResponse(user, profile);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found with id: " + userId));

        // Full update - all fields must be provided
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            profile.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null && !request.address().isBlank()) {
            profile.setAddress(request.address());
        }
        if (request.abilitiesDescription() != null && !request.abilitiesDescription().isBlank()) {
            profile.setAbilitiesDescription(request.abilitiesDescription());
        }
        if (request.organizationName() != null && !request.organizationName().isBlank()) {
            profile.setOrganizationName(request.organizationName());
        }

        userRepository.save(user);
        userProfileRepository.save(profile);

        return mapToUserProfileResponse(user, profile);
    }

    @Transactional
    public UserProfileResponse partialUpdateUserProfile(Long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found with id: " + userId));

        // Partial update - only update fields that are provided and not null/blank
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            profile.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null && !request.address().isBlank()) {
            profile.setAddress(request.address());
        }
        if (request.abilitiesDescription() != null) {
            profile.setAbilitiesDescription(request.abilitiesDescription());
        }
        if (request.organizationName() != null) {
            profile.setOrganizationName(request.organizationName());
        }

        userRepository.save(user);
        userProfileRepository.save(profile);

        return mapToUserProfileResponse(user, profile);
    }

}
