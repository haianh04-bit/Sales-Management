package com.codegym.services;

import com.codegym.models.ChatMessage;
import com.codegym.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getMessagesBetween(String user1, String user2) {
        return chatMessageRepository.getMessagesBetween(user1, user2);
    }

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public List<String> getUsersWhoMessagedAdmin() {
        return chatMessageRepository.findAllUsersWhoMessagedAdmin();
    }
}
