package com.chatnotify.room.dto;

import com.chatnotify.room.ChatRoom;
import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        String name,
        Long createdBy,
        LocalDateTime createdAt,
        long unreadCount
) {
    public static RoomResponse from(ChatRoom room, long unreadCount) {
        return new RoomResponse(room.getId(), room.getName(), room.getCreatedBy().getId(), room.getCreatedAt(), unreadCount);
    }
}
