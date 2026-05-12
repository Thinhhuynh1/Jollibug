package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.ChatRecord;
import vn.fastfood.entity.User;
import vn.fastfood.repository.ChatRecordRepository;
import vn.fastfood.repository.MonAnRepository;

@Controller
public class HomePageController {

    private final MonAnRepository monAnRepository;
    private final ChatRecordRepository chatRecordRepository;

    HomePageController(MonAnRepository monAnRepository,
                       ChatRecordRepository chatRecordRepository) {
        this.monAnRepository = monAnRepository;
        this.chatRecordRepository = chatRecordRepository;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("listMonAn", this.monAnRepository.findMonAnBestSeller(null, ""));
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    @GetMapping("/chat")
    public String getChatPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String conversationId = String.valueOf(user.getMaTK());
            List<ChatRecord> history = chatRecordRepository
                    .findByConversationIdOrderByCreatedAtAsc(conversationId);
            model.addAttribute("chatHistory", history);
        } else {
            model.addAttribute("chatHistory", List.of());
        }
        return "client/chat";
    }

    @GetMapping("/complaint")
    public String getComplaintPage() {
        return "client/complaint";
    }
}
