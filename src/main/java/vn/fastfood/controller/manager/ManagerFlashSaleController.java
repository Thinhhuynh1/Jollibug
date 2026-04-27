package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerFlashSaleController {
    @GetMapping("/manager/flash-sales")
    public String getFlashSalesPage() {
        return "manager/flash-sales/show";
    }

    @GetMapping("/manager/flash-sales/create")
    public String getFlashSalesCreatePage() {
        return "manager/flash-sales/create";
    }

    @GetMapping("/manager/flash-sales/detail")
    public String getFlashSalesDetailPage() {
        return "manager/flash-sales/detail";
    }

    @GetMapping("/manager/flash-sales/update")
    public String getFlashSalesUpdatePage() {
        return "manager/flash-sales/update";
    }

    @GetMapping("/manager/flash-sales/delete")
    public String getFlashSalesDeletePage() {
        return "manager/flash-sales/delete";
    }
}
