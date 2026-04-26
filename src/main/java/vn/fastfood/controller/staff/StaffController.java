package vn.fastfood.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StaffController {

    @GetMapping("/staff")
    public String getOrders() {
        return "/staff/orders";
    }

    @GetMapping("/staff/support")
    public String getSupportPage() {
        return "/staff/support";
    }

    @GetMapping("/staff/clients")
    public String getClientsPage() {
        return "/staff/clients/show";
    }

    @GetMapping("/staff/clients/detail")
    public String getClientDetailPage() {
        return "/staff/clients/detail";
    }

}
