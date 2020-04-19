package com.springchat.chat.controllers;

//import com.springchat.chat.util.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.nio.charset.Charset;
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
    public String processLoginFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        if(message.startsWith("log out ")) {
            String username = message.substring(8);
            usernames.remove(username);
            this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- logged out " + username);
            return "logged out";
        }
        else {
            if(usernames.contains(message)) return "Login failed: Username occupied";
            if(message.length() > 10) return "Login failed: Username too long";
            if(!Charset.forName("US-ASCII").newEncoder().canEncode(message)) return "Login failed: Username must be in english";
            if(!message.matches(".*[a-zA-Z]+.*")) return "Login failed: Username must contain letters";
            usernames.add(message);
            this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- logged in " + message);
            return "logged in";
        }
    }

    boolean containsNonEnglish(String value) {
        for (char c : value.toCharArray()) {
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) {
                return false;
            }
        }
        return true;
    }

    @MessageMapping("/userlist")
    @SendToUser("/queue/reply")
    public String returnUserlist(
            @Payload String message ) throws Exception {
        return usernamesToString();
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
