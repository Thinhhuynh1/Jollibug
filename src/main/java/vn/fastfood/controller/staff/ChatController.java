package vn.fastfood.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.fastfood.entity.ChatMessage;
import vn.fastfood.entity.ChatRecord;
import vn.fastfood.repository.ChatRecordRepository;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRecordRepository chatRecordRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
            ChatRecordRepository chatRecordRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatRecordRepository = chatRecordRepository;
    }

    /**
     * WebSocket endpoint: nhận tin nhắn từ client/staff,
     * lưu vào DB, rồi broadcast cho cả 2 phía.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        if (message == null || message.getConversationId() == null
                || message.getConversationId().isEmpty()) {
            return;
        }

        // Gán timestamp server-side
        message.setTimestamp(System.currentTimeMillis());

        // Lưu vào DB
        ChatRecord record = new ChatRecord();
        record.setConversationId(message.getConversationId());
        record.setSender(message.getSender());
        record.setSenderRole(message.getSenderRole());
        record.setContent(message.getContent());
        chatRecordRepository.save(record);

        // Broadcast cho tất cả subscriber của conversation này
        messagingTemplate.convertAndSend(
                "/topic/conversations/" + message.getConversationId(), message);

        // Broadcast thêm cho nhân viên để cập nhật sidebar / reload
        messagingTemplate.convertAndSend("/topic/staff/chat", message);
    }

    /**
     * REST API: lấy lịch sử chat của một conversation (dùng bởi JS khi trang load).
     */
    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ChatRecord> getChatHistory(
            @RequestParam("conversationId") String conversationId) {
        return chatRecordRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}
