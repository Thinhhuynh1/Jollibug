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

import vn.fastfood.entity.ChuongTrinhGiamGia;
import vn.fastfood.repository.ChuongTrinhGiamGiaRepository;

@Controller
public class ManagerPromotionController {
    private final ChuongTrinhGiamGiaRepository promotionRepository;

    public ManagerPromotionController(ChuongTrinhGiamGiaRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @GetMapping("/manager/promotions")
    public String getPromotionsPage(Model model,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }

        List<ChuongTrinhGiamGia> promotions = this.promotionRepository.searchByName(keyword);
        model.addAttribute("promotions", promotions);
        model.addAttribute("keyword", keyword);
        return "manager/promotions/show";
    }

    @GetMapping("/manager/promotions/create")
    public String getPromotionsCreatePage(Model model) {
        model.addAttribute("promotion", new ChuongTrinhGiamGia());
        return "manager/promotions/create";
    }

    @PostMapping("/manager/promotions/create")
    public String postPromotionsCreate(
            @RequestParam("tenCT") String tenCT,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        ChuongTrinhGiamGia promotion = new ChuongTrinhGiamGia();
        promotion.setTenCT(tenCT);
        promotion.setPhanTramGiam(phanTramGiam);
        promotion.setNgayBatDau(parseDateTime(startDate, LocalTime.MIN));
        promotion.setNgayKetThuc(parseDateTime(endDate, LocalTime.MAX));
        this.promotionRepository.save(promotion);
        return "redirect:/manager/promotions";
    }

    @GetMapping("/manager/promotions/detail")
    public String getPromotionsDetailPage(@RequestParam(value = "promotionID", required = false) Long promotionID,
            Model model) {
        if (promotionID == null) {
            return "redirect:/manager/promotions";
        }
        ChuongTrinhGiamGia promotion = this.promotionRepository.findById(promotionID).orElse(null);
        if (promotion == null) {
            return "redirect:/manager/promotions";
        }
        model.addAttribute("promotion", promotion);
        return "manager/promotions/detail";
    }

    @GetMapping("/manager/promotions/update")
    public String getPromotionsUpdatePage(@RequestParam(value = "promotionID", required = false) Long promotionID,
            Model model) {
        if (promotionID == null) {
            return "redirect:/manager/promotions";
        }
        ChuongTrinhGiamGia promotion = this.promotionRepository.findById(promotionID).orElse(null);
        if (promotion == null) {
            return "redirect:/manager/promotions";
        }
        model.addAttribute("promotion", promotion);
        return "manager/promotions/update";
    }

    @PostMapping("/manager/promotions/update")
    public String postPromotionsUpdate(
            @RequestParam("promotionID") Long promotionID,
            @RequestParam("tenCT") String tenCT,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        ChuongTrinhGiamGia promotion = this.promotionRepository.findById(promotionID).orElse(null);
        if (promotion == null) {
            return "redirect:/manager/promotions";
        }
        promotion.setTenCT(tenCT);
        promotion.setPhanTramGiam(phanTramGiam);
        promotion.setNgayBatDau(parseDateTime(startDate, LocalTime.MIN));
        promotion.setNgayKetThuc(parseDateTime(endDate, LocalTime.MAX));
        this.promotionRepository.save(promotion);
        return "redirect:/manager/promotions";
    }

    @GetMapping("/manager/promotions/delete")
    public String getPromotionsDeletePage(@RequestParam(value = "promotionID", required = false) Long promotionID,
            Model model) {
        if (promotionID == null) {
            return "redirect:/manager/promotions";
        }
        ChuongTrinhGiamGia promotion = this.promotionRepository.findById(promotionID).orElse(null);
        if (promotion == null) {
            return "redirect:/manager/promotions";
        }
        model.addAttribute("promotion", promotion);
        return "manager/promotions/delete";
    }

    @PostMapping("/manager/promotions/delete")
    public String postPromotionsDelete(@RequestParam("promotionID") Long promotionID) {
        this.promotionRepository.deleteById(promotionID);
        return "redirect:/manager/promotions";
    }

    private LocalDateTime parseDateTime(String dateValue, LocalTime fallbackTime) {
        if (dateValue == null || dateValue.isBlank()) {
            return null;
        }
        LocalDate date = LocalDate.parse(dateValue);
        return LocalDateTime.of(date, fallbackTime);
    }
}
