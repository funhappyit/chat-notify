package com.chatnotify.notification;

public record NotificationEvent(
        Long userId,
        Long roomId,
        long unreadCount
) {
}
