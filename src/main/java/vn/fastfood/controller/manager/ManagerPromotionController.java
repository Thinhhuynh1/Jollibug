package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerPromotionController {
    @GetMapping("/manager/promotions")
    public String getPromotionsPage() {
        return "manager/promotions/show";
    }

    @GetMapping("/manager/promotions/create")
    public String getPromotionsCreatePage() {
        return "manager/promotions/create";
    }

    @GetMapping("/manager/promotions/detail")
    public String getPromotionsDetailPage() {
        return "manager/promotions/detail";
    }

    @GetMapping("/manager/promotions/update")
    public String getPromotionsUpdatePage() {
        return "manager/promotions/update";
    }

    @GetMapping("/manager/promotions/delete")
    public String getPromotionsDeletePage() {
        return "manager/promotions/delete";
    }
}
