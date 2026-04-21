package vn.fastfood.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsersPage() {
        return "admin/manage-users";
    }

}
