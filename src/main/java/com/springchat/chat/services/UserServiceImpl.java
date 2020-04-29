package com.springchat.chat.services;

import com.springchat.chat.models.User;
import com.springchat.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;


    @Override
    public String usernamesToString() {
        StringBuilder builder = new StringBuilder();
        for(User user : repository.findAll()) {
            builder.append(user.getName() + ',');
        }
        return builder.toString();
    }
    @Override
    public User getUser(String username) {
        for(User user : repository.findAll()) {
            if(user.getName().equals(username)) return user;
        }
        return null;
    }
    @Override
    public User getUserFromMail(String mail) {
        for(User user : repository.findAll()) {
            if(user.getEmail().equals(mail)) return user;
        }
        return null;
    }
    @Override
    public boolean isUsernameOccupied(String username) {
        return getUser(username) != null;
    }
    @Override
    public boolean isMailOccupied(String mail) {
        return getUserFromMail(mail) != null;
    }
    @Override
    public void removeUser(String username) {
        removeUser(getUser(username));
    }
    @Override
    public void removeUser(User user) {
        repository.delete(user);
    }
    @Override
    public void addUser(String username, String password, String mail) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(mail);
        repository.save(user);
    }
}
