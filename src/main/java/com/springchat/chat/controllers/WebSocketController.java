package com.springchat.chat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WebSocketController {
    public List<String> usernames = new ArrayList<String>();
    private final SimpMessagingTemplate template;
    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }
    @MessageMapping("/send/message")
    public void onReceivedMessage(String message){
        this.template.convertAndSend("/chat", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- " + message);
    }
    @MessageMapping("/send/login")
    public void onReceivedLogin(String message){
        if(usernames.contains(message))
            this.template.convertAndSend("/login", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- Occupied");
        else {
            usernames.add(message);
            this.template.convertAndSend("/login", new SimpleDateFormat("HH:mm:ss").format(new Date())+ "- Logged in");
        }
    }
}
