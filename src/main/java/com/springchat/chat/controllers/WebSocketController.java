package com.springchat.chat.controllers;

//import com.springchat.chat.util.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate template;
    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    public List<String> usernames = new ArrayList<>();

    @MessageMapping("/login")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        if(usernames.contains(message)) {
            return "Occupied";
        }
        else{
            if(message.startsWith("disconnect ")) {
                String username = message.substring(11);
                usernames.remove(username);
                this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- disconnected " + username);
                return "disconnected";
            }
            else {
                usernames.add(message);
                this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- connected " + message);
                return usernamesToString();
            }
        }
    }

    public String usernamesToString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < usernames.size(); i++) {
            builder.append(usernames.get(i) + ',');
        }
        return builder.toString();
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
