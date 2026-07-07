package com.chatnotify.message;

import com.chatnotify.message.dto.MessageResponse;
import com.chatnotify.message.dto.MessageSendRequest;
import com.chatnotify.security.StompPrincipal;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/message")
    public void sendMessage(@Payload MessageSendRequest request, Principal principal) {
        StompPrincipal stompPrincipal = (StompPrincipal) principal;
        MessageResponse response = messageService.saveMessage(stompPrincipal.getUserId(), request.roomId(), request.content());
        messagingTemplate.convertAndSend("/sub/room/" + request.roomId(), response);
    }
}
