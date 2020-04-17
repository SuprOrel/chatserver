//package com.springchat.chat.util;
//
//import java.awt.print.PrinterIOException;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChatUser {
//    public static List<ChatUser> All = new ArrayList<ChatUser>();
//    public static void addChatUser(Principal principal, String username) {
//        All.add(new ChatUser(principal, username));
//    }
//    public static void removeChatUser(Principal principal) {
//        System.out.println(All.size());
//        for(int i = 0; i < All.size(); i++) {
//            System.out.println(i);
//            ChatUser cur = All.get(i);
//            System.out.println(cur == null);
//            System.out.println(cur.getPrincipal() == null);
//            System.out.println(principal == null);
//            if(cur.getPrincipal().equals(principal)) All.remove(i);
//        }
//    }
//    public static ChatUser getChatUser(Principal principal) {
//        for(int i = 0; i < All.size(); i++) {
//            ChatUser cur = All.get(i);
//            if(cur.getPrincipal().equals(principal)) return cur;
//        }
//        return null;
//    }
//    public static boolean isUsernameAvailable(String username) {
//        for(int i = 0; i < All.size(); i++) {
//            ChatUser cur = All.get(i);
//            if(cur.getUsername().equals(username)) return false;
//        }
//        return true;
//    }
//
//    Principal principal;
//    public Principal getPrincipal() {
//        return this.principal;
//    }
//    String username;
//    public String getUsername() {
//        return this.username;
//    }
//    public ChatUser(Principal principal, String username) {
//        this.principal = principal;
//        this.username = username;
//    }
//}
