package com.korayaks.chatapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korayaks.chatapp.model.*;
import com.korayaks.chatapp.service.GroupChatUsersService;
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
    @Autowired
    GroupChatUsersService groupChatUsersService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        if(message.getStatus().equals(Status.JOIN)){
            message.setMessage(groupChatUsersService.getGroupChatUsers(message.getSenderName()).getInGroup());
        }else if(message.getStatus().equals(Status.MESSAGE)){
            messageService.saveMessage(message);
        }
        return message;
    }
    @MessageMapping("/groupMessage")
    @SendTo("/chatroom/group")
    public Message receiveGroupMessage(@Payload Message message){
        System.out.println(message);
        if(message.getStatus().equals(Status.INFO)){
            groupChatUsersService.updateGroupChatUsers(message.getSenderName(),message.getMessage());
        }else if(message.getStatus().equals(Status.GROUP_MESSAGE)){
            messageService.saveMessage(message);
        }

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
    public IntroduceMessage introduce(@Payload IntroduceMessage introduceMessage) {
        simpMessagingTemplate.convertAndSendToUser(introduceMessage.getReceiverName(),"/client/introduce",introduceMessage);
        return introduceMessage;
    }

    @MessageMapping("/registerOrLogin")
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
    public List<User> userList(@Payload User user) throws JsonProcessingException {
        List<User> userList = userService.getAllUsers();
        String jsonUserList = objectMapper.writeValueAsString(userList);
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/userList", jsonUserList);
        return userList;
    }




}
