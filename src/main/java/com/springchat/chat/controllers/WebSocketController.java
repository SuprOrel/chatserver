package com.springchat.chat.controllers;

import com.springchat.chat.util.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate template;
    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/login")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        if(ChatUser.isUsernameAvailable(message)) {
            ChatUser.addChatUser(principal, message);
            return message;
        }
        else{
            return "Occupied";
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    @MessageMapping("/message")
    public void onReceivedMessage(String message){
        this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- " + message);
    }
}
