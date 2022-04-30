package com.korayaks.chatapp.service;

import com.korayaks.chatapp.model.GroupChatUsers;
import com.korayaks.chatapp.model.User;

public interface GroupChatUsersService {
    GroupChatUsers saveGroupChatUsers(User user);
    GroupChatUsers getGroupChatUsers(String username);
    GroupChatUsers updateGroupChatUsers(String username, String inGroup);
}
