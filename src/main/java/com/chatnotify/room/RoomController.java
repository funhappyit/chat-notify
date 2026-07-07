package com.chatnotify.room;

import com.chatnotify.message.MessageService;
import com.chatnotify.message.dto.MessageResponse;
import com.chatnotify.room.dto.RoomCreateRequest;
import com.chatnotify.room.dto.RoomResponse;
import com.chatnotify.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;
    private final MessageService messageService;

    public RoomController(RoomService roomService, MessageService messageService) {
        this.roomService = roomService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomCreateRequest request,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(principal.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        return ResponseEntity.ok(roomService.listRooms());
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        roomService.joinRoom(principal.getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        roomService.leaveRoom(principal.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> messages(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(messageService.getMessages(principal.getId(), id));
    }
}
