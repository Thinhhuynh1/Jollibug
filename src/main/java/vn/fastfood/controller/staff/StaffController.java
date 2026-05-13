package vn.fastfood.controller.staff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.ChatRecord;
import vn.fastfood.entity.User;
import vn.fastfood.repository.ChatRecordRepository;
import vn.fastfood.repository.UserRepository;

@Controller
public class StaffController {

    private final ChatRecordRepository chatRecordRepository;
    private final UserRepository userRepository;

    public StaffController(ChatRecordRepository chatRecordRepository,
            UserRepository userRepository) {
        this.chatRecordRepository = chatRecordRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/staff")
    public String getOrders() {
        return "redirect:/staff/orders/confirmed";
    }

    @GetMapping("/staff/orders/confirmed")
    public String getConfirmedOrdersPage(Model model) {
        model.addAttribute("orderTab", "confirmed");
        return "/staff/orders";
    }

    @GetMapping("/staff/orders/unconfirmed")
    public String getUnconfirmedOrdersPage(Model model) {
        model.addAttribute("orderTab", "unconfirmed");
        return "/staff/orders";
    }

    @GetMapping("/staff/orders/detail")
    public String getOrderDetailPage() {
        return "/staff/orders/detail";
    }

    @GetMapping("/staff/orders/update-status")
    public String getOrderUpdateStatusPage() {
        return "/staff/orders/update-status";
    }

    @GetMapping("/staff/orders/confirm")
    public String getOrderConfirmPage() {
        return "/staff/orders/confirm";
    }

    @GetMapping("/staff/support")
    public String getSupportPage(@RequestParam(value = "conversationId", required = false) String conversationId,
            Model model) {

        // --- Lấy danh sách conversation (client đã từng nhắn) ---
        List<String> convIds = chatRecordRepository.findDistinctConversationIds();

        // Build danh sách ticket: mỗi ticket gồm conversationId, tên khách, tin cuối
        List<Map<String, Object>> tickets = new ArrayList<>();
        for (String cid : convIds) {
            Map<String, Object> ticket = new LinkedHashMap<>();
            ticket.put("conversationId", cid);

            // Tìm tên khách dựa vào maTK (conversationId = maTK)
            String clientName = cid; // fallback
            try {
                long maTK = Long.parseLong(cid);
                User u = userRepository.findByMaTK(maTK);
                if (u != null)
                    clientName = u.getHoTen();
            } catch (NumberFormatException ignored) {
            }

            ticket.put("clientName", clientName);

            ChatRecord latest = chatRecordRepository.findTop1ByConversationIdOrderByCreatedAtDesc(cid);
            ticket.put("latestMessage", latest != null ? latest.getContent() : "");
            // Pre-format time for JSP (fmt:formatDate doesn't support LocalDateTime)
            if (latest != null && latest.getCreatedAt() != null) {
                ticket.put("latestTimeDisplay", latest.getCreatedAt()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                ticket.put("latestTimeDisplay", "");
            }

            tickets.add(ticket);
        }
        model.addAttribute("tickets", tickets);

        // --- Lịch sử chat của conversation đang chọn ---
        String activeConvId = conversationId;
        if ((activeConvId == null || activeConvId.isEmpty()) && !convIds.isEmpty()) {
            activeConvId = convIds.get(0);
        }

        String activeClientName = "";
        if (activeConvId != null) {
            try {
                long maTK = Long.parseLong(activeConvId);
                User u = userRepository.findByMaTK(maTK);
                if (u != null)
                    activeClientName = u.getHoTen();
            } catch (NumberFormatException ignored) {
            }

            List<ChatRecord> history = chatRecordRepository
                    .findByConversationIdOrderByCreatedAtAsc(activeConvId);
            model.addAttribute("chatHistory", history);
        } else {
            model.addAttribute("chatHistory", List.of());
        }

        model.addAttribute("activeConvId", activeConvId);
        model.addAttribute("activeClientName", activeClientName);
        model.addAttribute("onlineCount", convIds.size());

        return "/staff/support";
    }

    @PostMapping("/staff/support/chat/send")
    public String sendSupportChat(@RequestParam("message") String message) {
        return "redirect:/staff/support?tab=chat";
    }

    @PostMapping("/staff/support/complaint/reply")
    public String replyComplaint(@RequestParam("reply") String reply) {
        return "redirect:/staff/support?tab=complaint";
    }

    @PostMapping("/staff/support/review/reply")
    public String replyReview(@RequestParam("reply") String reply) {
        return "redirect:/staff/support?tab=review";
    }

    @GetMapping("/staff/clients")
    public String getClientsPage() {
        return "/staff/clients/show";
    }

    @GetMapping("/staff/clients/detail")
    public String getClientDetailPage() {
        return "/staff/clients/detail";
    }
}
