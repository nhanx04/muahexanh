package com.muahexanh.be.user.dto;

import com.muahexanh.be.user.UserRole;
import com.muahexanh.be.user.UserStatus;

public record UserProfileResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        String phoneNumber,
        String address,
        String abilitiesDescription,
        String organizationName,
        UserRole role,
        UserStatus status
) {
}
