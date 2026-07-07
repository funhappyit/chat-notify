package com.chatnotify.security;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final Long userId;
    private final String username;

    public StompPrincipal(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }
}
