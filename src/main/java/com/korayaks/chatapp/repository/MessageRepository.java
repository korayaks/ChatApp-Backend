package com.korayaks.chatapp.repository;

import com.korayaks.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesByReceiverName(String receiverName);
    Message findMessageById(Long id);
}
