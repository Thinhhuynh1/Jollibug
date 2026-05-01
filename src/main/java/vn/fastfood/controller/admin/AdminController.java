package vn.fastfood.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.User;
import vn.fastfood.repository.RoleRepository;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    AdminController(UserRepository userRepository,
            UserService userService,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsersPage(Model model,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> listUserActive;

        if ((role != null && !role.equals("All")) || (keyword != null && !keyword.isEmpty())) {
            listUserActive = this.userRepository.search(role, keyword, "ACTIVE");
        } else {
            listUserActive = this.userService.findByTrangThai("ACTIVE");
        }

        model.addAttribute("lisUsers", listUserActive);
        model.addAttribute("userTab", "active");
        model.addAttribute("selectedRole", role != null ? role : "All");
        model.addAttribute("keyword", keyword);

        return "admin/manage-users";
    }

    @GetMapping("/admin/users/block")
    public String getUsersBlockPage(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<User> listUserBlocked;

        if ((role != null && !role.equals("All")) || (keyword != null && !keyword.isEmpty())) {
            listUserBlocked = this.userRepository.search(role, keyword, "BANNED");
        } else {
            listUserBlocked = this.userService.findByTrangThai("BANNED");
        }

        model.addAttribute("lisUsers", listUserBlocked);
        model.addAttribute("userTab", "blocked");
        model.addAttribute("selectedRole", role != null ? role : "All");
        model.addAttribute("keyword", keyword);
        return "admin/manage-users";
    }

    @GetMapping("/admin/users/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/function/create";
    }

    @PostMapping("/admin/users/create")
    public String postCreateUsers(
            @RequestParam("HoTen") String hoTen,
            @RequestParam("Email") String email,
            @RequestParam("Password") String password,
            @RequestParam("SDT") String sdt,
            @RequestParam(value = "TrangThai", defaultValue = "ACTIVE") String trangThai,
            @RequestParam(value = "TenVT", required = false) String tenVT) {

        User user = new User();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setPassword(password);
        user.setSdt(sdt);
        user.setTrangThai(trangThai);

        if (tenVT != null && !tenVT.isEmpty()) {
            user.setVaiTro(this.userService.getRoleByName(tenVT));
        }

        this.userService.saveUser(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{maTk}")
    public String getDetailPage(@PathVariable long maTk, Model model) {
        User user = this.userRepository.findById(maTk).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTk", maTk);
        return "admin/function/detail";
    }

    @GetMapping("/admin/users/update/{maTk}")
    public String getUpdateUserPage(Model model,
            @PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        model.addAttribute("user", user);

        return "admin/function/update";
    }

    @PostMapping("/admin/users/update/{maTk}")
    public String postUpdateUser(
            @PathVariable long maTk,
            @RequestParam("HoTen") String hoTen,
            @RequestParam(value = "Password", required = false) String password,
            @RequestParam("SDT") String sdt,
            @RequestParam(value = "role", required = false) String role) {

        User user = this.userRepository.findById(maTk).orElse(null);
        if (user != null) {
            user.setHoTen(hoTen);
            user.setSdt(sdt);

            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }

            if (role != null && !role.isEmpty()) {
                user.setVaiTro(this.userService.getRoleByName(role));
            }

            this.userService.saveUser(user);
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{maTk}")
    public String getDeleteUserPage(Model model,
            @PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        model.addAttribute("user", user);

        return "admin/function/delete";
    }

    @PostMapping("/admin/users/delete/{maTk}")
    public String postDeleteUser(@PathVariable long maTk) {
        this.userService.deleteUser(maTk);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/ban/{maTk}")
    public String getBanUserPage(Model model,
            @PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTk", maTk);
        return "admin/function/ban";
    }

    @PostMapping("/admin/users/ban/{maTk}")
    public String postBanUser(@PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        user.setTrangThai("BANNED");
        this.userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/unban/{maTk}")
    public String getUnbanUserPage(Model model,
            @PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTk", maTk);
        return "admin/function/unban";
    }

    @PostMapping("/admin/users/unban/{maTk}")
    public String postUnbanUser(@PathVariable long maTk) {
        User user = this.userRepository.findById(maTk).orElse(null);
        user.setTrangThai("ACTIVE");
        this.userService.saveUser(user);
        return "redirect:/admin/users/block";
    }

}
