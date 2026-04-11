package com.muahexanh.be.auth.dto;

import com.muahexanh.be.user.UserRole;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String username,
        UserRole role
) {
}

