package com.korayaks.chatapp.controller;

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

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    UserService userService;

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
}
