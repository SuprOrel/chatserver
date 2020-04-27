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
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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
            String message) throws Exception {
        String[] split = message.split(",");
        String mail = split[0], password = split[1];
        User user = getUserFromMail(mail);
        if(user == null) return "Login failed: User not found";
        if(!user.getPassword().equals(password)) return "Login failed: Incorrect password";
        return "Logged in: " + user.getName();
//        if(message.startsWith("delete ")) {
//            String username = message.substring(7);
////            UserRepository.remove(username);
//            removeUser(username);
//            this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- Server:deleted " + username);
//            return "deleted";
//        }
//        else {
//            String[] info = message.split(",");
//            if(info[0].equals("Server")) return "Login failed: Illegal username";
//            User user = getUser(info[0]);
//            if(user == null) {
//                if (info[0].length() > 10) return "Login failed: Username too long";
//                if (!Charset.forName("US-ASCII").newEncoder().canEncode(message))
//                    return "Login failed: English only";
//                if (!info[0].matches(".*[a-zA-Z]+.*")) return "Login failed: Username must contain letters";
//                if (!info[1].matches(".*[a-zA-Z]+.*")) return "Login failed: Password must contain letters";
//                addUser(info[0], info[1]);
//                this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "- Server:logged in " + info[0]);
//            }
//            else {
//                if(!user.getPassword().equals(info[1])) return "Login failed: Incorrect password";
//            }
//            return "logged in";
//        }
    }

    @MessageMapping("/register")
    @SendToUser("/queue/reply")
    public String processRegisterFromClient(
            String message) throws Exception {
        String[] split = message.split(",");
        String mail = split[0], password = split[1], username = split[2];
        if (username.length() > 10) return "Register failed: Username longer then 10 letters";
        if (!username.matches(".*[a-zA-Z]+.*")) return "Register failed: Username must contain letters";
        if (!password.matches(".*[a-zA-Z]+.*")) return "Register failed: Password must contain letters";
        CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
        if(!encoder.canEncode(username)) return "Register failed: Username must be in english";
        if(!encoder.canEncode(password)) return "Register failed: Password must be in english";
        if(isUsernameOccupied(username)) return "Register failed: Username occupied";
        if(isMailOccupied(mail)) return "Register failed: Mail occupied";
        addUser(username, password, mail);
        this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "- Server:registerd " + username);
        System.out.println("registerd " + username + " " + password + " " + mail);
        return "Logged in: " + username;
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
    public User getUserFromMail(String mail) {
        for(User user : users.findAll()) {
            if(user.getEmail().equals(mail)) return user;
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

    public boolean isMailOccupied(String mail) {
        return getUserFromMail(mail) != null;
    }

    public void removeUser(String username) {
        removeUser(getUser(username));
    }
    public void removeUser(User user) {
        users.delete(user);
    }

    public void addUser(String username, String password, String mail) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(mail);
        users.save(user);
    }
}
