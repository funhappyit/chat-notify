package com.chatnotify.room;

import com.chatnotify.room.dto.RoomCreateRequest;
import com.chatnotify.room.dto.RoomResponse;
import com.chatnotify.user.User;
import com.chatnotify.user.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    public RoomService(ChatRoomRepository chatRoomRepository,
                        RoomMemberRepository roomMemberRepository,
                        UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RoomResponse createRoom(Long currentUserId, RoomCreateRequest request) {
        User creator = userRepository.getReferenceById(currentUserId);
        ChatRoom room = chatRoomRepository.save(new ChatRoom(request.name(), creator));
        roomMemberRepository.save(new RoomMember(room, creator));
        return RoomResponse.from(room);
    }

    public List<RoomResponse> listRooms() {
        return chatRoomRepository.findAll().stream().map(RoomResponse::from).toList();
    }

    @Transactional
    public void joinRoom(Long currentUserId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));

        if (roomMemberRepository.existsByRoomIdAndUserId(roomId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 참여 중인 채팅방입니다.");
        }

        User user = userRepository.getReferenceById(currentUserId);
        roomMemberRepository.save(new RoomMember(room, user));
    }

    @Transactional
    public void leaveRoom(Long currentUserId, Long roomId) {
        RoomMember member = roomMemberRepository.findByRoomIdAndUserId(roomId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "참여 중인 채팅방이 아닙니다."));
        roomMemberRepository.delete(member);
    }
}
