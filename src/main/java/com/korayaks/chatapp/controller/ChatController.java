package com.korayaks.chatapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korayaks.chatapp.model.Message;
import com.korayaks.chatapp.model.User;
import com.korayaks.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        System.out.println(message.toString());
        return message;
    }

    @MessageMapping("/registerOrLogin")
    @SendTo("/client/registerOrLogin")
    public boolean registerOrLogin(@Payload User user){
        Message message = new Message();
        boolean result = userService.registerOrLogin(user);
        message.setMessage(String.valueOf(result));
        System.out.println("girdi ");
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/registerOrLogin", message);
        return result;
    }

    @MessageMapping("/userList")
    @SendTo("/client/userList")
    public List<User> userList(@Payload User user) throws JsonProcessingException {
        List<User> userList = userService.getAllUsers();
        String jsonUserList = objectMapper.writeValueAsString(userList);
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/userList", jsonUserList);
        return userList;
    }
}
