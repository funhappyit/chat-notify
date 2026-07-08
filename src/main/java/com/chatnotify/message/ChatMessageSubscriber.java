package com.chatnotify.message;

import com.chatnotify.message.dto.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public ChatMessageSubscriber(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MessageResponse response = objectMapper.readValue(message.getBody(), MessageResponse.class);
            messagingTemplate.convertAndSend("/sub/room/" + response.roomId(), response);
        } catch (IOException e) {
            throw new IllegalStateException("채팅 메시지 역직렬화에 실패했습니다.", e);
        }
    }
}
