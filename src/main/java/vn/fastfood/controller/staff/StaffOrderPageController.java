package vn.fastfood.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffOrderPageController {

    @GetMapping("/staff/orders")
    public String staffOrdersPage() {
        return "staff/orders/staff-orders";
    }

    @GetMapping("staff/order-detail")
    public String staffOrderDetailPage() {
        return "staff/orders/staff-order-detail";
    }

    @GetMapping("/order-staff/orders")
    public String staffOrdersPageOld() {
        return "redirect:/staff/orders";
    }

    @GetMapping("/order-staff/orders/detail")
    public String staffOrderDetailPageOld() {
        return "redirect:/staff/orders";
    }
}