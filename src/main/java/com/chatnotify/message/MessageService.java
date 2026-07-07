package com.chatnotify.message;

import com.chatnotify.message.dto.MessageResponse;
import com.chatnotify.room.ChatRoom;
import com.chatnotify.room.ChatRoomRepository;
import com.chatnotify.room.RoomMemberRepository;
import com.chatnotify.user.User;
import com.chatnotify.user.UserRepository;
import java.util.List;
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

    public MessageService(MessageRepository messageRepository,
                           RoomMemberRepository roomMemberRepository,
                           ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public List<MessageResponse> getMessages(Long currentUserId, Long roomId) {
        requireMember(currentUserId, roomId);
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
        return MessageResponse.from(saved);
    }

    private void requireMember(Long userId, Long roomId) {
        if (!roomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 멤버만 접근할 수 있습니다.");
        }
    }
}
