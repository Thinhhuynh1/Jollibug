package vn.fastfood.controller.client;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.SupportRequest;
import vn.fastfood.entity.ChiTietHoTro;
import vn.fastfood.entity.User;
import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.ChiTietHoTroRepository;
import vn.fastfood.service.YeuCauHoTroService;

@RestController
@RequestMapping("/api/support")
public class SupportApiController {

    private final YeuCauHoTroService yeuCauHoTroService;
    private final ChiTietHoTroRepository chiTietHoTroRepository;

    public SupportApiController(YeuCauHoTroService yeuCauHoTroService,
            ChiTietHoTroRepository chiTietHoTroRepository) {
        this.yeuCauHoTroService = yeuCauHoTroService;
        this.chiTietHoTroRepository = chiTietHoTroRepository;
    }

    @PostMapping("/complaints")
    public ResponseEntity<Map<String, Object>> sendComplaint(@RequestBody SupportRequest request,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui long dang nhap de gui khieu nai."));
        }
        if (request.getTieuDe() == null || request.getTieuDe().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Tieu de khieu nai khong duoc de trong."));
        }
        if (request.getNoiDung() == null || request.getNoiDung().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Noi dung khieu nai khong duoc de trong."));
        }

        YeuCauHoTro yeuCau = yeuCauHoTroService.createYeuCau(user.getMaTK(),
                request.getTieuDe().trim(), request.getNoiDung().trim());

        ChiTietHoTro chiTiet = new ChiTietHoTro();
        chiTiet.setYeuCau(yeuCau);
        chiTiet.setNguoiGui(user);
        chiTiet.setNoiDung(request.getNoiDung().trim());
        chiTietHoTroRepository.save(chiTiet);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Da gui khieu nai thanh cong.",
                "maYC", yeuCau.getMaYC()));
    }
}
