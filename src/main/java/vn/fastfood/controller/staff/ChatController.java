package vn.fastfood.controller.staff;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.fastfood.entity.ChatMessage;
import vn.fastfood.entity.ChiTietHoTro;
import vn.fastfood.entity.User;
import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.ChiTietHoTroRepository;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.repository.YeuCauHoTroRepository;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChiTietHoTroRepository chiTietRepo;
    private final YeuCauHoTroRepository yeuCauRepo;
    private final UserRepository userRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
            ChiTietHoTroRepository chiTietRepo,
            YeuCauHoTroRepository yeuCauRepo,
            UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chiTietRepo = chiTietRepo;
        this.yeuCauRepo = yeuCauRepo;
        this.userRepository = userRepository;
    }

    /**
     * WebSocket endpoint: nhận tin nhắn từ Client hoặc Staff,
     * lưu vào bảng CHITIETHOTRO, rồi broadcast cho cả 2 phía.
     *
     * JS gửi lên: { maYC, maTKGui, vaiTroGui, noiDung, tenNguoiGui }
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        if (message == null || message.getMaYC() == null)
            return;

        // Gán timestamp server-side
        message.setTimestamp(System.currentTimeMillis());

        // Lấy YeuCauHoTro từ DB
        YeuCauHoTro yeuCau = yeuCauRepo.findById(message.getMaYC()).orElse(null);
        if (yeuCau == null)
            return;

        // Lấy User gửi
        User nguoiGui = userRepository.findByMaTK(message.getMaTKGui());
        if (nguoiGui == null)
            return;

        // Nếu yêu cầu còn Pending và nhân viên gửi → chuyển sang Processing
        if ("Pending".equals(yeuCau.getTrangThai()) && "NhanVien".equals(message.getVaiTroGui())) {
            yeuCau.setNhanVien(nguoiGui);
            yeuCau.setTrangThai("Processing");
            yeuCauRepo.save(yeuCau);
        }

        // Lưu tin nhắn vào CHITIETHOTRO (Cột VaiTroGui đã được lược bỏ vì tính toán ảo)
        ChiTietHoTro chiTiet = new ChiTietHoTro();
        chiTiet.setYeuCau(yeuCau);
        chiTiet.setNguoiGui(nguoiGui);
        chiTiet.setNoiDung(message.getNoiDung());
        chiTietRepo.save(chiTiet);

        // Broadcast cho cả Client & Staff đang xem phòng chat này
        messagingTemplate.convertAndSend("/topic/chat/" + message.getMaYC(), message);

        // Broadcast thêm cho tất cả Staff để cập nhật sidebar
        messagingTemplate.convertAndSend("/topic/staff/chat", message);
    }

    /**
     * REST API: lấy lịch sử chat của một yêu cầu.
     * Dùng khi JS cần lazy-load tin nhắn sau khi chọn phòng chat.
     */
    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ChiTietHoTro> getChatHistory(@RequestParam("maYC") Long maYC) {
        return chiTietRepo.findByYeuCau_MaYCOrderByNgayGuiAsc(maYC);
    }

    /**
     * REST API: đóng yêu cầu hỗ trợ (Staff bấm "Hoàn thành").
     */
    @GetMapping("/api/chat/close")
    public String closeRequest(@RequestParam("maYC") Long maYC) {
        yeuCauRepo.findById(maYC).ifPresent(yc -> {
            yc.setTrangThai("Done");
            yeuCauRepo.save(yc);
        });
        return "redirect:/staff/support";
    }
}
