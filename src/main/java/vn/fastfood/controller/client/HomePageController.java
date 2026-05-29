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
import vn.fastfood.service.KhuyenMaiService;

@Controller
public class HomePageController {

    private final MonAnRepository monAnRepository;
    private final YeuCauHoTroRepository yeuCauRepo;
    private final ChiTietHoTroRepository chiTietRepo;
    private final KhuyenMaiService khuyenMaiService;

    HomePageController(
            MonAnRepository monAnRepository,
            YeuCauHoTroRepository yeuCauRepo,
            ChiTietHoTroRepository chiTietRepo,
            KhuyenMaiService khuyenMaiService) {
        this.monAnRepository = monAnRepository;
        this.yeuCauRepo = yeuCauRepo;
        this.chiTietRepo = chiTietRepo;
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<vn.fastfood.entity.MonAn> list = monAnRepository.findMonAnBestSeller(null, "");
        khuyenMaiService.applyKhuyenMai(list);
        model.addAttribute("listMonAn", list);
        return "client/homepage";
    }

    @GetMapping("/chat")
    public String getChatPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<YeuCauHoTro> myRequests = yeuCauRepo
                .findByMaTKKH(user.getMaTK())
                .stream()
                .filter(y -> !"DONE".equals(y.getTrangThai()))
                .toList();

        if (myRequests.isEmpty()) {
            model.addAttribute("yeuCau", null);
            model.addAttribute("chatHistory", List.of());
        } else {
            YeuCauHoTro activeYC = myRequests.get(0);
            List<ChiTietHoTro> history = chiTietRepo.findByYeuCau_MaYCOrderByNgayGuiAsc(activeYC.getMaYC());
            model.addAttribute("yeuCau", activeYC);
            model.addAttribute("chatHistory", history);
        }

        return "client/chat";
    }

    @PostMapping("/chat/create")
    public String createTicket(
            @RequestParam("tieuDe") String tieuDe,
            @RequestParam("noiDung") String noiDung,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        YeuCauHoTro newYC = new YeuCauHoTro();
        newYC.setKhachHang(user);
        newYC.setTieuDe(tieuDe);
        newYC.setNoiDung(noiDung);
        newYC.setTrangThai("PENDING");
        yeuCauRepo.save(newYC);

        ChiTietHoTro chiTiet = new ChiTietHoTro();
        chiTiet.setYeuCau(newYC);
        chiTiet.setNguoiGui(user);
        chiTiet.setNoiDung(noiDung);
        chiTietRepo.save(chiTiet);

        return "redirect:/chat";
    }
}
