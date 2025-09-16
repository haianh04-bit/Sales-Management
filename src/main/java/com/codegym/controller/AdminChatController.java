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
@RequestMapping("/admin/chat")
public class AdminChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/inbox")
    public String inbox(@RequestParam(required = false) String user, Model model) {
        String admin = "ADMIN"; // ép cố định

        if (user != null) {
            List<ChatMessage> messages = chatMessageService.getMessagesBetween(admin, user);
            model.addAttribute("messages", messages);
            model.addAttribute("withUser", user);
        }

        model.addAttribute("users", chatMessageService.getUsersWhoMessagedAdmin());
        return "chat/admin-inbox";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String receiver,
                              @RequestParam String content) {
        String sender = "ADMIN"; // ép cố định

        if (content == null || content.trim().isEmpty()) {
            return "redirect:/admin/chat/inbox?user=" + receiver;
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        chatMessageService.saveMessage(message);
        return "redirect:/admin/chat/inbox?user=" + receiver;
    }
}
