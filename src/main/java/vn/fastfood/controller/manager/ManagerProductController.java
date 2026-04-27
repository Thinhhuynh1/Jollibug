package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerProductController {
    @GetMapping("/manager/products")
    public String getProductsPage() {
        return "manager/products/show";
    }

    @GetMapping("/manager/products/create")
    public String getProductsAddPage() {
        return "manager/products/create";
    }

    @GetMapping("/manager/products/detail")
    public String getProductsDetailPage() {
        return "manager/products/detail";
    }

    @GetMapping("/manager/products/update")
    public String getProductsUpdatePage() {
        return "manager/products/update";
    }

    @GetMapping("/manager/products/delete")
    public String getProductsDeletePage() {
        return "manager/products/delete";
    }
}
