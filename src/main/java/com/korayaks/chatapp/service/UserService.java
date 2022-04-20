package com.korayaks.chatapp.service;

import com.korayaks.chatapp.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    boolean registerOrLogin(User user);
}
