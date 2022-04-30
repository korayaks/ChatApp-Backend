package com.korayaks.chatapp.service.impl;

import com.korayaks.chatapp.model.GroupChatUsers;
import com.korayaks.chatapp.model.User;
import com.korayaks.chatapp.repository.GroupChatUsersRepository;
import com.korayaks.chatapp.service.GroupChatUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupChatUsersServiceImpl implements GroupChatUsersService {
    @Autowired
    GroupChatUsersRepository groupChatUsersRepository;
    @Override
    public GroupChatUsers saveGroupChatUsers(User user) {
        GroupChatUsers groupChatUser = new GroupChatUsers();
        groupChatUser.setInGroup("false");
        groupChatUser.setUsername(user.getUsername());
        groupChatUsersRepository.save(groupChatUser);
        return groupChatUser;
    }

    @Override
    public GroupChatUsers getGroupChatUsers(String username) {
        return groupChatUsersRepository.findGroupChatUsersByUsername(username);
    }

    @Override
    public GroupChatUsers updateGroupChatUsers(String username, String inGroup) {
        GroupChatUsers groupChatUsers = groupChatUsersRepository.findGroupChatUsersByUsername(username);
        if(groupChatUsers != null){
            groupChatUsers.setInGroup(inGroup);
            groupChatUsersRepository.save(groupChatUsers);
        }
        return  groupChatUsers;
    }
}
