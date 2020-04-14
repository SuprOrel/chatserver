package com.springchat.chat.listeners;

import java.util.Optional;

import com.springchat.chat.util.ChatUser;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class PresenceEventListener {

    public PresenceEventListener() {
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("connected " + event.getUser());
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        ChatUser.removeChatUser(event.getUser());
        System.out.println("disconnected");
    }
}
