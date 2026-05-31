package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.ChiTietHoTro;
import vn.fastfood.entity.User;
import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.ChiTietHoTroRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.repository.YeuCauHoTroRepository;

@Controller
public class HomePageController {

    private final MonAnRepository monAnRepository;
    private final YeuCauHoTroRepository yeuCauRepo;
    private final ChiTietHoTroRepository chiTietRepo;
    private final vn.fastfood.service.PromotionService promotionService;

    HomePageController(MonAnRepository monAnRepository,
            YeuCauHoTroRepository yeuCauRepo,
            ChiTietHoTroRepository chiTietRepo,
            vn.fastfood.service.PromotionService promotionService) {
        this.monAnRepository = monAnRepository;
        this.yeuCauRepo = yeuCauRepo;
        this.chiTietRepo = chiTietRepo;
        this.promotionService = promotionService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<vn.fastfood.entity.MonAn> list = this.monAnRepository.findMonAnBestSeller(null, "");
        this.promotionService.applyPromotions(list);
        model.addAttribute("listMonAn", list);
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    /**
     * Trang Chat hỗ trợ phía Client.
     * - Nếu client chưa có yêu cầu nào đang mở → redirect sang trang tạo yêu cầu
     * mới.
     * - Nếu có → lấy yêu cầu mới nhất để hiển thị lịch sử chat.
     */
    @GetMapping("/chat")
    public String getChatPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // Lấy danh sách yêu cầu đang mở của user (Pending | Processing)
        List<YeuCauHoTro> myRequests = yeuCauRepo
                .findByMaTKKH(user.getMaTK())
                .stream()
                .filter(y -> !"Done".equals(y.getTrangThai()))
                .toList();

        if (myRequests.isEmpty()) {
            // Không tạo sẵn yêu cầu hỗ trợ, chỉ khởi tạo view rỗng
            model.addAttribute("yeuCau", null);
            model.addAttribute("chatHistory", List.of());
        } else {
            YeuCauHoTro activeYC = myRequests.get(0);
            List<ChiTietHoTro> history = chiTietRepo
                    .findByYeuCau_MaYCOrderByNgayGuiAsc(activeYC.getMaYC());
            model.addAttribute("yeuCau", activeYC);
            model.addAttribute("chatHistory", history);
        }

        return "client/chat";
    }

    @PostMapping("/chat/create")
    public String createTicket(@RequestParam("tieuDe") String tieuDe,
                               @RequestParam("noiDung") String noiDung,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // Tạo yêu cầu
        YeuCauHoTro newYC = new YeuCauHoTro();
        newYC.setKhachHang(user);
        newYC.setTieuDe(tieuDe);
        newYC.setNoiDung(noiDung);
        newYC.setTrangThai("Pending");
        yeuCauRepo.save(newYC);

        // Lưu Nội dung thành tin nhắn đầu tiên luôn để hiển thị trong chat
        ChiTietHoTro chiTiet = new ChiTietHoTro();
        chiTiet.setYeuCau(newYC);
        chiTiet.setNguoiGui(user);
        chiTiet.setNoiDung(noiDung);
        chiTietRepo.save(chiTiet);

        return "redirect:/chat";
    }

    @GetMapping("/complaints")
    public String getComplaintsPage() {
        return "client/complaints";
    }

}
