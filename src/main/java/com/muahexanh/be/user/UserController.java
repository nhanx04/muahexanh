package com.muahexanh.be.user;

import com.muahexanh.be.user.dto.UpdateUserProfileRequest;
import com.muahexanh.be.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMMUNITY_LEADER', 'UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        UserProfileResponse response = userService.getUserProfile(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMMUNITY_LEADER', 'UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        UserProfileResponse response = userService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMMUNITY_LEADER', 'UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMMUNITY_LEADER', 'UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> partialUpdateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse response = userService.partialUpdateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse response = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/profile")
    @PreAuthorize("hasRole('UNI_ADMIN')")
    public ResponseEntity<UserProfileResponse> partialUpdateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse response = userService.partialUpdateUserProfile(id, request);
        return ResponseEntity.ok(response);
    }

}
