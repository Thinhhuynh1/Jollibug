package vn.fastfood.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsersPage(Model model) {
        model.addAttribute("userTab", "active");
        return "admin/manage-users";
    }

    @GetMapping("/admin/users/block")
    public String getUsersBlockPage(Model model) {
        model.addAttribute("userTab", "blocked");
        return "admin/manage-users";
    }

    @GetMapping("/admin/users/create")
    public String getCreateUserPage() {
        return "admin/function/create";
    }

    @GetMapping("/admin/users/detail")
    public String getDetailPage() {
        return "admin/function/detail";
    }

    @GetMapping("/admin/users/update")
    public String getUpdateUserPage() {
        return "admin/function/update";
    }

    @GetMapping("/admin/users/delete")
    public String getDeleteUserPage() {
        return "admin/function/delete";
    }

    @GetMapping("/admin/users/ban")
    public String getBanUserPage() {
        return "admin/function/ban";
    }

    @GetMapping("/admin/users/unban")
    public String getUnbanUserPage() {
        return "admin/function/unban";
    }

}
