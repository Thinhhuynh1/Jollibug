package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManagerController {
    @GetMapping("/manager")
    public String getDashBoardPage() {
        return "manager/dashboard";
    }

    @GetMapping("/manager/categories")
    public String getCategoriesPage() {
        return "manager/categories";
    }

    @GetMapping("/manager/products")
    public String getProductsPage() {
        return "manager/products";
    }

}
