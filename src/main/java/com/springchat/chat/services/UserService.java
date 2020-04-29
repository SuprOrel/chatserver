package com.springchat.chat.services;

import com.springchat.chat.models.User;

public interface UserService {
    public String usernamesToString();

    public User getUser(String username);
    public User getUserFromMail(String mail);

    public boolean isUsernameOccupied(String username);

    public boolean isMailOccupied(String mail);
    public void removeUser(String username);
    public void removeUser(User user);

    public void addUser(String username, String password, String mail);
}
