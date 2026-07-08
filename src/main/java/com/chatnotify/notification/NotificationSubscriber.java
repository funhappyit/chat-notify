package com.chatnotify.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationSubscriber implements MessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationSubscriber(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            NotificationEvent event = objectMapper.readValue(message.getBody(), NotificationEvent.class);
            notificationService.notifyIfLocal(event);
        } catch (IOException e) {
            throw new IllegalStateException("알림 이벤트 역직렬화에 실패했습니다.", e);
        }
    }
}
