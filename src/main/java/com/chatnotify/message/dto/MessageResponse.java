package com.chatnotify.message.dto;

import com.chatnotify.message.Message;
import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long roomId,
        Long senderId,
        String senderUsername,
        String content,
        LocalDateTime sentAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRoom().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
