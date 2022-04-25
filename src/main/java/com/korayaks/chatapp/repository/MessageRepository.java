package com.korayaks.chatapp.repository;

import com.korayaks.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
