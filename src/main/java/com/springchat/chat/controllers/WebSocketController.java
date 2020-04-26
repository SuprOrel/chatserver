package com.springchat.chat.controllers;

//import com.springchat.chat.util.ChatUser;
import com.springchat.chat.User;
import com.springchat.chat.UserRepository;
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

    @Autowired
    UserRepository users;

    @MessageMapping("/login")
    @SendToUser("/queue/reply")
    public String processLoginFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        if(message.startsWith("delete ")) {
            String username = message.substring(7);
//            UserRepository.remove(username);
            removeUser(username);
            this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- Server:deleted " + username);
            return "deleted";
        }
        else {
            String[] info = message.split(",");
            if(info[0].equals("Server")) return "Login failed: Illegal username";
            User user = getUser(info[0]);
            if(user == null) {
                if (info[0].length() > 10) return "Login failed: Username too long";
                if (!Charset.forName("US-ASCII").newEncoder().canEncode(message))
                    return "Login failed: English only";
                if (!info[0].matches(".*[a-zA-Z]+.*")) return "Login failed: Username must contain letters";
                if (!info[1].matches(".*[a-zA-Z]+.*")) return "Login failed: Password must contain letters";
                addUser(info[0], info[1]);
                this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "- Server:logged in " + info[0]);
            }
            else {
                if(!user.getPassword().equals(info[1])) return "Login failed: Incorrect password";
            }
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

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    @MessageMapping("/message")
    public void onReceivedMessage(String message){
        this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- " + message);
    }

    public String usernamesToString() {
        StringBuilder builder = new StringBuilder();
        for(User user : users.findAll()) {
            builder.append(user.getName() + ',');
        }
        return builder.toString();
    }

    public User getUser(String username) {
        for(User user : users.findAll()) {
            if(user.getName().equals(username)) return user;
        }
        return null;
    }
//    public int getUserId(String username) {
//        for(User user : users.findAll()) {
//            if(user.getName().equals(username)) return user.getId();
//        }
//        return -1;
//    }

    public boolean isUsernameOccupied(String username) {
        return getUser(username) != null;
    }

    public void removeUser(String username) {
        removeUser(getUser(username));
    }
    public void removeUser(User user) {
        users.delete(user);
    }

    public void addUser(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        users.save(user);
    }
}
