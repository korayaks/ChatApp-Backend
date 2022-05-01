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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public Message receiveMessage(@Payload Message message) {
        System.out.println(message);
        if(message.getStatus().equals(Status.JOIN)){
            message.setMessage(groupChatUsersService.getGroupChatUsers(message.getSenderName()).getInGroup());
        }else if(message.getStatus().equals(Status.PUBLIC_MESSAGE)){
            messageService.saveMessage(message);
        }

        return message;
    }
    @MessageMapping("/groupMessage")
    @SendTo("/chatroom/group")
    public Message receiveGroupMessage(@Payload Message message) {
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
        if(message.getStatus().equals(Status.PRIVATE_MESSAGE)){
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        }else if(message.getStatus().equals(Status.PUBLIC_MESSAGE)){
            simpMessagingTemplate.convertAndSend("/chatroom/public",message);
        }else if(message.getStatus().equals(Status.GROUP_MESSAGE)){
            simpMessagingTemplate.convertAndSend("/chatroom/group",message);
        }
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
        boolean result = userService.registerOrLogin(user); // kullanıcı bağlan butonuna tıkladığında
        message.setMessage(String.valueOf(result));         // kayıt mı giriş mi yoksa başarısız giriş mi bunu belirliyoruz.
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/registerOrLogin", message);//kayıt veya giriş başarılı ise frontend'e mesaj yolluyoruz ve kullanıcı bu sayede giriş yapıyor
        List<Message> unreadMessages = messageService.getUnreadMessages(user.getUsername());//kullanıcı giriş yaptığında tüm okunmamış mesajlarını listeye atıyotuz
        //Okunmamış mesajları tarihe göre sıralıyorum
        Comparator<Message> comparator = (c1, c2) -> {
          return c1.getDate().compareTo(c2.getDate());
        };
        Collections.sort(unreadMessages,comparator);
        if(unreadMessages.size()>0){ //tüm okunmamış mesajları tarihe göre sıralı olarak kullanıcıya yolluyorum.
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

    @MessageMapping("/usersInGroupList")
    public List<User> usersInGroupList(@Payload User user) throws JsonProcessingException {
        List<User> userList = userService.getAllUsers();
        List<GroupChatUsers> groupChatUsers = groupChatUsersService.usersInGroup("true");
        List<User> inGroupUsers = new ArrayList<>();
        for (User userItem :
                userList) {
            for (GroupChatUsers groupItem:
                 groupChatUsers) {
                if(groupItem.getUsername().equals(userItem.getUsername())){
                    if(groupItem.getInGroup().equals("true")){
                        inGroupUsers.add(userItem);
                    }
                }
            }
        }
        System.out.println("In group users : " + inGroupUsers);
        String jsonUserList = objectMapper.writeValueAsString(inGroupUsers);
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/usersInGroupList", jsonUserList);
        return userList;
    }


}
