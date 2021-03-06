package com.korayaks.chatapp.repository;

import com.korayaks.chatapp.model.GroupChatUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupChatUsersRepository extends JpaRepository<com.korayaks.chatapp.model.GroupChatUsers, Long> {
    GroupChatUsers findGroupChatUsersByUsername(String username);
    List<GroupChatUsers> findGroupChatUsersByInGroup(String inGroup);
}
