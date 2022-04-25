package com.korayaks.chatapp.service.impl;

import com.korayaks.chatapp.model.Message;
import com.korayaks.chatapp.repository.MessageRepository;
import com.korayaks.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageRepository messageRepository;
    @Override
    public void saveMessage(Message message) {
        Message newMessage = new Message();
        newMessage.setMessage(message.getMessage());
        newMessage.setSenderName(message.getSenderName());
        newMessage.setDate(LocalDateTime.now());
        newMessage.setReceived(message.getReceived());
        newMessage.setStatus(message.getStatus());
        newMessage.setReceiverName(message.getReceiverName());
        messageRepository.save(newMessage);
    }
}
