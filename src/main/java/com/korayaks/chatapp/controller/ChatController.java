package com.korayaks.chatapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korayaks.chatapp.model.IntroduceMessage;
import com.korayaks.chatapp.model.Message;
import com.korayaks.chatapp.model.User;
import com.korayaks.chatapp.service.MessageService;
import com.korayaks.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MessageService messageService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        messageService.saveMessage(message);
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) throws InterruptedException {
        Thread.sleep(1000);
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        messageService.saveMessage(message);
        System.out.println("Gönderilen mesaj : " + message);
        return message;
    }

    @MessageMapping("/introduce")
    @SendTo("/client/introduce")
    public IntroduceMessage introduce(@Payload IntroduceMessage introduceMessage) {
        simpMessagingTemplate.convertAndSendToUser(introduceMessage.getReceiverName(),"/client/introduce",introduceMessage);
        return introduceMessage;
    }

    @MessageMapping("/registerOrLogin")
    @SendTo("/client/registerOrLogin")
    public Message registerOrLogin(@Payload User user) throws InterruptedException {
        Message message = new Message();
        boolean result = userService.registerOrLogin(user);
        message.setMessage(String.valueOf(result));
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/registerOrLogin", message);

        List<Message> unreadMessages = messageService.getUnreadMessages(user.getUsername());
        System.out.println(unreadMessages);
        if(unreadMessages.size()>0){
            for (Message unreadMessage :
                    unreadMessages) {
                recMessage(unreadMessage);
            }
        }
        return message;
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
