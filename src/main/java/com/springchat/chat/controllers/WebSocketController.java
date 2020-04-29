package com.springchat.chat.controllers;

//import com.springchat.chat.util.ChatUser;
import com.springchat.chat.models.User;
import com.springchat.chat.services.UserService;
import com.springchat.chat.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate template;
    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Autowired
    UserService userService;

    @MessageMapping("/login")
    @SendToUser("/queue/reply")
    public String processLoginFromClient(
            String message) throws Exception {
        String[] split = message.split(",");
        String mail = split[0], password = split[1];
        User user = userService.getUserFromMail(mail);
        if(user == null) return "Login failed: User not found";
        if(!user.getPassword().equals(password)) return "Login failed: Incorrect password";
        return "Logged in: " + user.getName() + "," + user.getMessageCount();
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
        if (!isValidEmail(mail)) return "Register failed: Email address invalid";
        CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
        if(!encoder.canEncode(username)) return "Register failed: Username must be in english";
        if(!encoder.canEncode(password)) return "Register failed: Password must be in english";
        if(userService.isUsernameOccupied(username)) return "Register failed: Username occupied";
        if(userService.isMailOccupied(mail)) return "Register failed: Mail occupied";
        userService.addUser(username, password, mail);
        this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "- Server:registerd " + username);
        System.out.println("registerd " + username + " " + password + " " + mail);
        return "Logged in: " + username;
    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    @MessageMapping("/userlist")
    @SendToUser("/queue/reply")
    public String returnUserlist(
            @Payload String message ) throws Exception {
        return userService.usernamesToString();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    @MessageMapping("/message")
    public void onReceivedMessage(String message){
        User user = userService.getUser(message.substring(0, message.indexOf(':')));
        user.setMessageCount(user.getMessageCount() + 1);
        userService.update(user);
        this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- " + message);
    }

    @MessageMapping("/command")
    public void onReceivedCommand(String message){
        if(message.startsWith("delete ")) {
            String username = message.substring(7);
            userService.removeUser(username);
            this.template.convertAndSend("/global", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- Server:deleted " + username);
        }
        else if(message.startsWith("messages ")) {
            String[] values = message.substring(9).split(" ");
            User user = userService.getUser(values[0]);
            user.setMessageCount(Integer.parseInt(values[1]));
            userService.update(user);
            System.out.println("set " + values[0] + "'s message count to " + values[1]);
        }
    }
}
