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
        System.out.println("G??nderilen mesaj : " + message);
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
        boolean result = userService.registerOrLogin(user); // kullan??c?? ba??lan butonuna t??klad??????nda
        message.setMessage(String.valueOf(result));         // kay??t m?? giri?? mi yoksa ba??ar??s??z giri?? mi bunu belirliyoruz.
        simpMessagingTemplate.convertAndSendToUser(user.getUsername(),"/client/registerOrLogin", message);//kay??t veya giri?? ba??ar??l?? ise frontend'e mesaj yolluyoruz ve kullan??c?? bu sayede giri?? yap??yor
        List<Message> unreadMessages = messageService.getUnreadMessages(user.getUsername());//kullan??c?? giri?? yapt??????nda t??m okunmam???? mesajlar??n?? listeye at??yotuz
        //Okunmam???? mesajlar?? tarihe g??re s??ral??yorum
        Comparator<Message> comparator = (c1, c2) -> {
          return c1.getDate().compareTo(c2.getDate());
        };
        Collections.sort(unreadMessages,comparator);
        if(unreadMessages.size()>0){ //t??m okunmam???? mesajlar?? tarihe g??re s??ral?? olarak kullan??c??ya yolluyorum.
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
