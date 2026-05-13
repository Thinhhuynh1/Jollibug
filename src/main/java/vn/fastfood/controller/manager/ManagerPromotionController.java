package vn.fastfood.controller.manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.ChuongTrinhGiamGia;
import vn.fastfood.entity.DanhMuc;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.ChuongTrinhGiamGiaRepository;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;

@Controller
public class ManagerPromotionController {
    private final ChuongTrinhGiamGiaRepository promotionRepository;
    private final DanhMucRepository danhMucRepository;
    private final MonAnRepository monAnRepository;

    public ManagerPromotionController(ChuongTrinhGiamGiaRepository promotionRepository,
            DanhMucRepository danhMucRepository,
            MonAnRepository monAnRepository) {
        this.promotionRepository = promotionRepository;
        this.danhMucRepository = danhMucRepository;
        this.monAnRepository = monAnRepository;
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
        model.addAttribute("danhMucList", this.danhMucRepository.findAll());
        model.addAttribute("monAnList", this.monAnRepository.findAll());
        model.addAttribute("selectedMonAnIds", List.of());
        return "manager/promotions/create";
    }

    @PostMapping("/manager/promotions/create")
    public String postPromotionsCreate(
            @RequestParam("tenCT") String tenCT,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "phamViApDung", required = false, defaultValue = "ALL") String phamViApDung,
            @RequestParam(value = "maDM", required = false) Long maDM,
            @RequestParam(value = "selectedMonAnIds", required = false) List<Long> selectedMonAnIds) {
        ChuongTrinhGiamGia promotion = new ChuongTrinhGiamGia();
        promotion.setTenCT(tenCT);
        promotion.setPhanTramGiam(phanTramGiam);
        promotion.setPhamViApDung(phamViApDung);
        promotion.setMaDM("CATEGORY".equals(phamViApDung) ? maDM : null);
        promotion.setDanhSachMonAn(formatSelectedMonAnIds("ITEM".equals(phamViApDung) ? selectedMonAnIds : null));
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
        model.addAttribute("phamViLabel", promotion.getPhamViApDungDisplay());
        if (promotion.getPhamViApDung() != null && promotion.getPhamViApDung().equals("CATEGORY") && promotion.getMaDM() != null) {
            DanhMuc category = this.danhMucRepository.findById(promotion.getMaDM()).orElse(null);
            model.addAttribute("apDungDanhMuc", category);
        }
        if (promotion.getPhamViApDung() != null && promotion.getPhamViApDung().equals("ITEM")) {
            List<Long> ids = promotion.getDanhSachMonAnIds();
            List<MonAn> items = ids.isEmpty() ? List.of() : this.monAnRepository.findAllById(ids);
            model.addAttribute("apDungMonAnList", items);
        }
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
        model.addAttribute("danhMucList", this.danhMucRepository.findAll());
        model.addAttribute("monAnList", this.monAnRepository.findAll());
        model.addAttribute("selectedMonAnIds", promotion.getDanhSachMonAnIds());
        return "manager/promotions/update";
    }

    @PostMapping("/manager/promotions/update")
    public String postPromotionsUpdate(
            @RequestParam("promotionID") Long promotionID,
            @RequestParam("tenCT") String tenCT,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "phamViApDung", required = false, defaultValue = "ALL") String phamViApDung,
            @RequestParam(value = "maDM", required = false) Long maDM,
            @RequestParam(value = "selectedMonAnIds", required = false) List<Long> selectedMonAnIds) {
        ChuongTrinhGiamGia promotion = this.promotionRepository.findById(promotionID).orElse(null);
        if (promotion == null) {
            return "redirect:/manager/promotions";
        }
        promotion.setTenCT(tenCT);
        promotion.setPhanTramGiam(phanTramGiam);
        promotion.setPhamViApDung(phamViApDung);
        promotion.setMaDM("CATEGORY".equals(phamViApDung) ? maDM : null);
        promotion.setDanhSachMonAn(formatSelectedMonAnIds("ITEM".equals(phamViApDung) ? selectedMonAnIds : null));
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

    private String formatSelectedMonAnIds(List<Long> selectedMonAnIds) {
        if (selectedMonAnIds == null || selectedMonAnIds.isEmpty()) {
            return "";
        }
        return selectedMonAnIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
