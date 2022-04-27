package com.korayaks.chatapp.service.impl;

import com.korayaks.chatapp.model.Message;
import com.korayaks.chatapp.model.User;
import com.korayaks.chatapp.repository.MessageRepository;
import com.korayaks.chatapp.repository.UserRepository;
import com.korayaks.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public boolean registerOrLogin(User user) {
        if(userRepository.findUserByUsername(user.getUsername()) == null)
        {
            System.out.println("Kullanıcı kayit oldu : " + user);
            userRepository.save(user);
            return true;
        }else if((userRepository.findUserByUsername(user.getUsername())).getPassword().equals(user.getPassword()))
        {
            System.out.println("Kullanıcı giriş yaptı : " + user);
            return true;
        }else{
            System.out.println("Kullanıcı yanlış giriş yaptı : " + user);
            return false;
        }
    }

}
