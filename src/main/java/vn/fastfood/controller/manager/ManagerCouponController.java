package vn.fastfood.controller.manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.repository.MaGiamGiaRepository;

@Controller
public class ManagerCouponController {
    private final MaGiamGiaRepository couponRepository;

    public ManagerCouponController(MaGiamGiaRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @GetMapping("/manager/coupons")
    public String getCouponsPage(Model model,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null) {
            keyword = keyword.trim();

            if (keyword.isEmpty()) {
                keyword = null;
            }
        }

        List<MaGiamGia> coupons = this.couponRepository.searchByCode(keyword);
        model.addAttribute("coupons", coupons);
        model.addAttribute("keyword", keyword);
        return "manager/coupons/show";
    }

    @GetMapping("/manager/coupons/create")
    public String getCouponsCreatePage(Model model) {
        model.addAttribute("coupon", new MaGiamGia());
        return "manager/coupons/create";
    }

    @PostMapping("/manager/coupons/create")
    public String postCouponsCreate(
            @RequestParam("code") String code,
            @RequestParam("loaiGiam") String loaiGiam,
            @RequestParam("mucGiam") Double mucGiam,
            @RequestParam(value = "dieuKien", required = false) Double dieuKien,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        MaGiamGia coupon = new MaGiamGia();
        coupon.setTenMa(code.toUpperCase());
        coupon.setLoaiGiam(loaiGiam);
        coupon.setMucGiam(mucGiam);
        if (dieuKien == null || dieuKien <= 0) {
            coupon.setDieuKien(null);
        }
        else {
            coupon.setDieuKien(dieuKien);
        }
        coupon.setSoLuong(soLuong);
        coupon.setMoTa(moTa);
        if (startDate == null || startDate.isBlank()) {
            coupon.setNgayBatDau(null);
        }
        else {
            coupon.setNgayBatDau(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN));
        }

        if (endDate == null || endDate.isBlank()) {
            coupon.setNgayKetThuc(null);
        }
        else {
            coupon.setNgayKetThuc(LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX));
        }
        this.couponRepository.save(coupon);
        return "redirect:/manager/coupons";
    }

    @GetMapping("/manager/coupons/detail")
    public String getCouponsDetailPage(@RequestParam(value = "couponID", required = false) Long couponID,
            Model model) {
        if (couponID == null) {
            return "redirect:/manager/coupons";
        }

        MaGiamGia coupon = this.couponRepository.findById(couponID).orElse(null);
        if (coupon == null) {
            return "redirect:/manager/coupons";
        }

        model.addAttribute("coupon", coupon);
        return "manager/coupons/detail";
    }

    @GetMapping("/manager/coupons/update")
    public String getCouponsUpdatePage(@RequestParam(value = "couponID", required = false) Long couponID,
            Model model) {
        if (couponID == null) {
            return "redirect:/manager/coupons";
        }

        MaGiamGia coupon = this.couponRepository.findById(couponID).orElse(null);
        if (coupon == null) {
            return "redirect:/manager/coupons";
        }

        model.addAttribute("coupon", coupon);
        return "manager/coupons/update";
    }

    @PostMapping("/manager/coupons/update")
    public String postCouponsUpdate(
            @RequestParam("couponID") Long couponID,
            @RequestParam("code") String code,
            @RequestParam("loaiGiam") String loaiGiam,
            @RequestParam("mucGiam") Double mucGiam,
            @RequestParam(value = "dieuKien", required = false) Double dieuKien,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        MaGiamGia coupon = this.couponRepository.findById(couponID).orElse(null);
        if (coupon == null) {
            return "redirect:/manager/coupons";
        }

        coupon.setTenMa(code.toUpperCase());
        coupon.setLoaiGiam(loaiGiam);
        coupon.setMucGiam(mucGiam);
        if (dieuKien == null || dieuKien <= 0) {
            coupon.setDieuKien(null);
        }
        else {
            coupon.setDieuKien(dieuKien);
        }
        coupon.setSoLuong(soLuong);
        coupon.setMoTa(moTa);
        if (startDate == null || startDate.isBlank()) {
            coupon.setNgayBatDau(null);
        }
        else {
            coupon.setNgayBatDau(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN));
        }

        if (endDate == null || endDate.isBlank()) {
            coupon.setNgayKetThuc(null);
        }
        else {
            coupon.setNgayKetThuc(LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX));
        }
        this.couponRepository.save(coupon);
        return "redirect:/manager/coupons";
    }

    @GetMapping("/manager/coupons/delete")
    public String getCouponsDeletePage(@RequestParam(value = "couponID", required = false) Long couponID,
            Model model) {
        if (couponID == null) {
            return "redirect:/manager/coupons";
        }

        MaGiamGia coupon = this.couponRepository.findById(couponID).orElse(null);
        if (coupon == null) {
            return "redirect:/manager/coupons";
        }

        model.addAttribute("coupon", coupon);
        return "manager/coupons/delete";
    }

    @PostMapping("/manager/coupons/delete")
    public String postCouponsDelete(@RequestParam("couponID") Long couponID) {
        this.couponRepository.deleteById(couponID);
        return "redirect:/manager/coupons";
    }
}
