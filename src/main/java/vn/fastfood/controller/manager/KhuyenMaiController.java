package vn.fastfood.controller.manager;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.fastfood.entity.ChuongTrinhKhuyenMai;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.KhuyenMaiService;

@Controller
public class KhuyenMaiController {
    private final KhuyenMaiService khuyenMaiService;
    private final MonAnRepository monAnRepository;

    public KhuyenMaiController(KhuyenMaiService khuyenMaiService,
            MonAnRepository monAnRepository) {
        this.khuyenMaiService = khuyenMaiService;
        this.monAnRepository = monAnRepository;
    }

    @GetMapping("/manager/promotions")
    public String getPromotionsPage(Model model,
            @RequestParam(value = "keyword", required = false) String keyword) {
        model.addAttribute("promotions", khuyenMaiService.findKhuyenMai(keyword));
        model.addAttribute("keyword", keyword == null ? null : keyword.trim());
        return "manager/promotions/show";
    }

    @GetMapping("/manager/promotions/create")
    public String getPromotionsCreatePage(Model model) {
        model.addAttribute("promotion", new ChuongTrinhKhuyenMai());
        model.addAttribute("monAnList", monAnRepository.findAll());
        model.addAttribute("selectedMonAnIds", List.of());
        return "manager/promotions/create";
    }

    @PostMapping("/manager/promotions/create")
    public String postPromotionsCreate(@RequestParam("tenKM") String tenKM,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "phamViApDung", required = false, defaultValue = "ALL") String phamViApDung,
            @RequestParam(value = "selectedMonAnIds", required = false) List<Long> selectedMonAnIds,
            RedirectAttributes redirectAttributes) {
        try {
            khuyenMaiService.createKhuyenMai(
                    tenKM,
                    phanTramGiam,
                    phamViApDung,
                    selectedMonAnIds,
                    startDate,
                    endDate
            );
            return redirectToList();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manager/promotions/create";
        }
    }

    @GetMapping("/manager/promotions/detail")
    public String getPromotionsDetailPage(@RequestParam(value = "promotionID", required = false) Long maKM,
            Model model) {
        ChuongTrinhKhuyenMai khuyenMai = khuyenMaiService.findKhuyenMaiById(maKM);
        if (khuyenMai == null) {
            return redirectToList();
        }
        model.addAttribute("promotion", khuyenMai);
        model.addAttribute("apDungMonAnList", khuyenMaiService.findMonAnApDung(khuyenMai));
        return "manager/promotions/detail";
    }

    @GetMapping("/manager/promotions/update")
    public String getPromotionsUpdatePage(@RequestParam(value = "promotionID", required = false) Long maKM,
            Model model) {
        ChuongTrinhKhuyenMai khuyenMai = khuyenMaiService.findKhuyenMaiById(maKM);
        if (khuyenMai == null) {
            return redirectToList();
        }

        model.addAttribute("promotion", khuyenMai);
        model.addAttribute("monAnList", monAnRepository.findAll());
        model.addAttribute("selectedMonAnIds", khuyenMai.getMonAnDuocApDungIds());
        return "manager/promotions/update";
    }

    @PostMapping("/manager/promotions/update")
    public String postPromotionsUpdate(@RequestParam("promotionID") Long maKM,
            @RequestParam("tenKM") String tenKM,
            @RequestParam("phanTramGiam") Double phanTramGiam,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "phamViApDung", required = false, defaultValue = "ALL") String phamViApDung,
            @RequestParam(value = "selectedMonAnIds", required = false) List<Long> selectedMonAnIds,
            RedirectAttributes redirectAttributes) {
        try {
            ChuongTrinhKhuyenMai khuyenMai = khuyenMaiService.updateKhuyenMai(
                    maKM,
                    tenKM,
                    phanTramGiam,
                    phamViApDung,
                    selectedMonAnIds,
                    startDate,
                    endDate
            );
            if (khuyenMai == null) {
                return redirectToList();
            }
            return redirectToList();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/manager/promotions/update?promotionID=" + maKM;
        }
    }

    @GetMapping("/manager/promotions/delete")
    public String getPromotionsDeletePage(@RequestParam(value = "promotionID", required = false) Long maKM,
            Model model) {
        ChuongTrinhKhuyenMai khuyenMai = khuyenMaiService.findKhuyenMaiById(maKM);
        if (khuyenMai == null) {
            return redirectToList();
        }

        model.addAttribute("promotion", khuyenMai);
        return "manager/promotions/delete";
    }

    @PostMapping("/manager/promotions/delete")
    public String postPromotionsDelete(@RequestParam("promotionID") Long maKM) {
        khuyenMaiService.deleteKhuyenMai(maKM);
        return redirectToList();
    }

    private String redirectToList() {
        return "redirect:/manager/promotions";
    }
}
