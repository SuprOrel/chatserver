package com.springchat.chat.services;

import com.springchat.chat.models.User;

public interface UserService {
    String usernamesToString();
    User getUser(String username);
    User getUserFromMail(String mail);
    boolean isUsernameOccupied(String username);
    boolean isMailOccupied(String mail);
    void removeUser(String username);
    void removeUser(User user);
    User addUser(String username, String password, String mail);
    void update(User user);
}
