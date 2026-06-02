package vn.fastfood.controller.staff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.ChiTietHoTro;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.ChiTietHoTroRepository;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.repository.YeuCauHoTroRepository;
import vn.fastfood.model.Order;
import vn.fastfood.service.OrderService;

@Controller
public class StaffController {

    private final YeuCauHoTroRepository yeuCauRepo;
    private final ChiTietHoTroRepository chiTietRepo;
    private final UserRepository userRepository;
    private final OrderService orderService = new OrderService();

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

    //-------------------------------------------------------------------
    //Qly khach hang 
    //-------------------------------------------------------------------
    @GetMapping("/staff/clients")
    public String getClientsPage(Model model) {
        List<User> clients = userRepository.findByVaiTro_TenVT("CLIENT");
        model.addAttribute("clients", clients);
        return "/staff/clients/show";
    }

    @GetMapping("/staff/clients/detail")
    public String getClientDetailPage(@RequestParam("clientId") long clientId, Model model) {
        User client = userRepository.findByMaTK(clientId);
        if (client == null) {
            return "redirect:/staff/clients";
        }

        List<Order> orders = orderService.getOrdersByMaKH(clientId);
        DiaChi defaultAddress = getDefaultAddress(client.getDiaChi());

        int totalOrders = orders.size();
        int completedOrders = 0;
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Order order : orders) {
            if (order.getThanhTien() != null) {
                totalSpent = totalSpent.add(order.getThanhTien());
            }

            String status = order.getTrangThaiDon();
            if (status != null && "DELIVERED".equalsIgnoreCase(status.trim())) {
                completedOrders++;
            }
        }

        model.addAttribute("client", client);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("recentOrders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("paymentSummary", buildPaymentSummary(orders));

        return "/staff/clients/detail";
    }

    private DiaChi getDefaultAddress(List<DiaChi> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }

        for (DiaChi address : addresses) {
            if (address != null && address.isDefaultAddress()) {
                return address;
            }
        }

        return addresses.get(0);
    }

    private List<String> buildPaymentSummary(List<Order> orders) {
        List<String> methods = new ArrayList<>();

        for (Order order : orders) {
            String method = order.getTenPT();
            if (method == null || method.trim().isEmpty()) {
                method = mapPaymentMethod(order.getMaPT());
            }

            if (method != null && !method.isBlank() && !methods.contains(method)) {
                methods.add(method);
            }
        }

        return methods;
    }

    private String mapPaymentMethod(String maPT) {
        if (maPT == null) {
            return null;
        }

        String mapPT = maPT.trim().toUpperCase();
        switch (mapPT) {
            case "COD":
                return "Thanh toán khi nhận hàng";
            case "BANK":
                return "Chuyển khoản ngân hàng";
            case "EWALLET":
                return "Ví điện tử";
            case "CREDIT_CARD":
                return "Thẻ tín dụng / Ghi nợ";
            default:
                return maPT;
        }
    }
}
