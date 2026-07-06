package com.chatnotify.auth.dto;

public record SignupResponse(
        Long id,
        String username,
        String email
) {
}
