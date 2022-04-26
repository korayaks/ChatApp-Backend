package com.korayaks.chatapp.service;

import com.korayaks.chatapp.model.Message;

import java.util.List;

public interface MessageService {
    void saveMessage(Message message);
    List<Message> getUnreadMessages(String username);
}
