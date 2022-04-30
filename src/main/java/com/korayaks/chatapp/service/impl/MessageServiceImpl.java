package com.korayaks.chatapp.service.impl;

import com.korayaks.chatapp.model.Message;
import com.korayaks.chatapp.model.Status;
import com.korayaks.chatapp.repository.MessageRepository;
import com.korayaks.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageRepository messageRepository;
    @Override
    public void saveMessage(Message message) {
        if(messageRepository.findMessageById(message.getId()) == null){
                Message newMessage = new Message();
                newMessage.setMessage(message.getMessage());
                newMessage.setSenderName(message.getSenderName());
                newMessage.setDate(LocalDateTime.now());
                newMessage.setStatus(message.getStatus());
            if(message.getReceiverName() != null){
                newMessage.setReceiverName(message.getReceiverName());
                newMessage.setReceived(message.getReceived());
                messageRepository.save(newMessage);
                System.out.println("Mesaj kaydedildi. (Private mesaj)");
            }else if(message.getMessage() != null){
                if(message.getStatus().equals(Status.MESSAGE)){
                    newMessage.setReceiverName("PUBLIC_MESSAGE");
                }else if(message.getStatus().equals(Status.GROUP_MESSAGE)){
                    newMessage.setReceiverName("GROUP_MESSAGE");
                }
                newMessage.setReceived("Yes");
                messageRepository.save(newMessage);
                System.out.println("Mesaj kaydedildi. (Public mesaj)");
            }
        }else{
            message.setReceived("Yes");
            messageRepository.save(message);
        }
    }

    @Override
    public List<Message> getUnreadMessages(String username) {
        //get unread messages
        List<Message> allReceivedMessages = messageRepository.findMessagesByReceiverName(username);
        List<Message> unreadMessages = new ArrayList<>();
        for (Message message :
                allReceivedMessages) {
            if(message.getReceived().equals("No")){
                unreadMessages.add(message);
            }
        }
        return unreadMessages;
    }
}
