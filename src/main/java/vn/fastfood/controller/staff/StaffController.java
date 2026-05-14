package vn.fastfood.controller.staff;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.ChiTietHoTro;
import vn.fastfood.entity.User;
import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.ChiTietHoTroRepository;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.repository.YeuCauHoTroRepository;

@Controller
public class StaffController {

    private final YeuCauHoTroRepository yeuCauRepo;
    private final ChiTietHoTroRepository chiTietRepo;
    private final UserRepository userRepository;

    public StaffController(YeuCauHoTroRepository yeuCauRepo,
                           ChiTietHoTroRepository chiTietRepo,
                           UserRepository userRepository) {
        this.yeuCauRepo = yeuCauRepo;
        this.chiTietRepo = chiTietRepo;
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
    public String getOrderDetailPage() { return "/staff/orders/detail"; }

    @GetMapping("/staff/orders/update-status")
    public String getOrderUpdateStatusPage() { return "/staff/orders/update-status"; }

    @GetMapping("/staff/orders/confirm")
    public String getOrderConfirmPage() { return "/staff/orders/confirm"; }

    // ----------------------------------------------------------------
    // SUPPORT PAGE
    // ----------------------------------------------------------------
    @GetMapping("/staff/support")
    public String getSupportPage(
            @RequestParam(value = "maYC", required = false) Long maYC,
            @RequestParam(value = "tab", defaultValue = "chat") String tab,
            Model model) {

        // Danh sách yêu cầu còn active (Pending + Processing)
        List<YeuCauHoTro> tickets = yeuCauRepo.findActiveRequests();
        model.addAttribute("tickets", tickets);
        model.addAttribute("onlineCount", tickets.size());

        // Nếu không chọn maYC thì lấy phòng đầu tiên
        YeuCauHoTro activeYC = null;
        if (maYC != null) {
            activeYC = yeuCauRepo.findById(maYC).orElse(null);
        } else if (!tickets.isEmpty()) {
            activeYC = tickets.get(0);
        }

        if (activeYC != null) {
            List<ChiTietHoTro> history = chiTietRepo
                    .findByYeuCau_MaYCOrderByNgayGuiAsc(activeYC.getMaYC());
            model.addAttribute("chatHistory", history);
            model.addAttribute("activeYC", activeYC);
            model.addAttribute("activeConvId", activeYC.getMaYC());
            model.addAttribute("activeClientName",
                    activeYC.getKhachHang() != null ? activeYC.getKhachHang().getHoTen() : "");
        } else {
            model.addAttribute("chatHistory", List.of());
            model.addAttribute("activeYC", null);
            model.addAttribute("activeConvId", null);
            model.addAttribute("activeClientName", "");
        }

        model.addAttribute("supportTab", tab);
        return "/staff/support";
    }

    @PostMapping("/staff/support/review/reply")
    public String replyReview(@RequestParam("reply") String reply) {
        return "redirect:/staff/support?tab=review";
    }

    @GetMapping("/staff/clients")
    public String getClientsPage() { return "/staff/clients/show"; }

    @GetMapping("/staff/clients/detail")
    public String getClientDetailPage() { return "/staff/clients/detail"; }
}
