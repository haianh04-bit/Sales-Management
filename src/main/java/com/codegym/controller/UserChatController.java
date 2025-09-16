package com.codegym.controller;

import com.codegym.models.ChatMessage;
import com.codegym.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/chat")
public class UserChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/inbox")
    public String inbox(Authentication authentication, Model model) {
        String username = authentication.getName();
        String admin = "ADMIN";

        List<ChatMessage> messages = chatMessageService.getMessagesBetween(username, admin);
        model.addAttribute("messages", messages);
        model.addAttribute("withUser", admin);
        return "chat/user-inbox";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String content,
                              Authentication authentication) {
        String sender = authentication.getName();
        String receiver = "ADMIN";

        if (content == null || content.trim().isEmpty()) {
            return "redirect:/user/chat/inbox";
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        chatMessageService.saveMessage(message);
        return "redirect:/user/chat/inbox";
    }

}
