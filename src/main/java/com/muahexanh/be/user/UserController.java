package com.muahexanh.be.user;

import com.muahexanh.be.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}
