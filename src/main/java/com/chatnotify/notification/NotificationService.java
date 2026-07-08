package com.chatnotify.notification;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationService {

    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> emitters.remove(userId, emitter));
        emitter.onError((e) -> emitters.remove(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId, emitter);
        }

        return emitter;
    }

    public void notifyIfLocal(NotificationEvent event) {
        SseEmitter emitter = emitters.get(event.userId());
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event().name("unread").data(event));
        } catch (IOException e) {
            emitters.remove(event.userId(), emitter);
        }
    }
}
