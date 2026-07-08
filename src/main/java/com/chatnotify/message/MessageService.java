package com.chatnotify.message;

import com.chatnotify.config.RedisConfig;
import com.chatnotify.message.dto.MessageResponse;
import com.chatnotify.notification.NotificationEvent;
import com.chatnotify.room.ChatRoom;
import com.chatnotify.room.ChatRoomRepository;
import com.chatnotify.room.RoomMember;
import com.chatnotify.room.RoomMemberRepository;
import com.chatnotify.user.User;
import com.chatnotify.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public MessageService(MessageRepository messageRepository,
                           RoomMemberRepository roomMemberRepository,
                           ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository,
                           StringRedisTemplate redisTemplate,
                           ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<MessageResponse> getMessages(Long currentUserId, Long roomId) {
        requireMember(currentUserId, roomId);
        messageRepository.markAsRead(roomId, currentUserId);
        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public MessageResponse saveMessage(Long senderId, Long roomId, String content) {
        requireMember(senderId, roomId);
        ChatRoom room = chatRoomRepository.getReferenceById(roomId);
        User sender = userRepository.getReferenceById(senderId);
        Message saved = messageRepository.save(new Message(room, sender, content));
        MessageResponse response = MessageResponse.from(saved);

        publishMessage(response);
        notifyOtherMembers(roomId, senderId);

        return response;
    }

    private void publishMessage(MessageResponse response) {
        try {
            redisTemplate.convertAndSend(RedisConfig.CHAT_CHANNEL, objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("메시지 발행에 실패했습니다.", e);
        }
    }

    private void notifyOtherMembers(Long roomId, Long senderId) {
        for (RoomMember member : roomMemberRepository.findByRoomId(roomId)) {
            Long memberId = member.getUser().getId();
            if (memberId.equals(senderId)) {
                continue;
            }

            long unreadCount = messageRepository.countByRoomIdAndSenderIdNotAndReadFalse(roomId, memberId);
            NotificationEvent event = new NotificationEvent(memberId, roomId, unreadCount);

            try {
                redisTemplate.convertAndSend(RedisConfig.NOTIFICATION_CHANNEL, objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("알림 발행에 실패했습니다.", e);
            }
        }
    }

    private void requireMember(Long userId, Long roomId) {
        if (!roomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 멤버만 접근할 수 있습니다.");
        }
    }
}
