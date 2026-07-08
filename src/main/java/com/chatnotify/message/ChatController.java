package com.chatnotify.message;

import com.chatnotify.message.dto.MessageSendRequest;
import com.chatnotify.security.StompPrincipal;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/message")
    public void sendMessage(@Payload MessageSendRequest request, Principal principal) {
        StompPrincipal stompPrincipal = (StompPrincipal) principal;
        messageService.saveMessage(stompPrincipal.getUserId(), request.roomId(), request.content());
    }
}
