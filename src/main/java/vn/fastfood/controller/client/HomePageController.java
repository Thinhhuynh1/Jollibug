package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.PromotionService;
import java.util.List;
import vn.fastfood.entity.MonAn;

@Controller
public class HomePageController {
    // home,about,contact

    private final MonAnRepository monAnRepository;
    private final PromotionService promotionService;

    public HomePageController(MonAnRepository monAnRepository, PromotionService promotionService) {
        this.monAnRepository = monAnRepository;
        this.promotionService = promotionService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<MonAn> listMonAn = this.monAnRepository.findMonAnBestSeller(null, "");
        this.promotionService.applyPromotions(listMonAn);
        model.addAttribute("listMonAn", listMonAn);
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    @GetMapping("/chat")
    public String getContactPage() {
        return "client/chat";
    }

    @GetMapping("/complaint")
    public String getComplaintPage() {
        return "client/complaint";
    }

}
