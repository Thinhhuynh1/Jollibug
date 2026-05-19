package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.ChatRequest;
import vn.fastfood.dto.ChatResponse;
import vn.fastfood.service.ChatbotService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String message = request.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ChatResponse("Vui lòng nhập câu hỏi."));
        }

        String reply = chatbotService.ask(message);

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
