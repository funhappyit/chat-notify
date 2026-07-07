package com.chatnotify.message.dto;

public record MessageSendRequest(
        Long roomId,
        String content
) {
}
