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
import vn.fastfood.entity.UserStatus;
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
    public String getDashboard(Model model) {
        model.addAttribute("countAdmin", this.userRepository.count("Admin", null));
        model.addAttribute("countManager", this.userRepository.count("Manager", null));
        model.addAttribute("countStaff", this.userRepository.count("Staff", null));
        model.addAttribute("countClient", this.userRepository.count("Client", null));
        model.addAttribute("countActive", this.userRepository.count(null, UserStatus.ACTIVE));
        model.addAttribute("countBan", this.userRepository.count(null, UserStatus.BANNED));

        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsersPage(Model model,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> listUserActive;

        if ((role != null && !role.equals("All")) || (keyword != null && !keyword.isEmpty())) {
            listUserActive = this.userRepository.search(role, keyword, UserStatus.ACTIVE);
        } else {
            listUserActive = this.userService.findByTrangThai(UserStatus.ACTIVE);
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
            listUserBlocked = this.userRepository.search(role, keyword, UserStatus.BANNED);
        } else {
            listUserBlocked = this.userService.findByTrangThai(UserStatus.BANNED);
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
            @RequestParam(value = "TrangThai", defaultValue = "ACTIVE") UserStatus trangThai,
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

    @GetMapping("/admin/users/{maTK}")
    public String getDetailPage(@PathVariable long maTK, Model model) {
        User user = this.userRepository.findById(maTK).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTK", maTK);
        return "admin/function/detail";
    }

    @GetMapping("/admin/users/update/{maTK}")
    public String getUpdateUserPage(Model model,
            @PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        model.addAttribute("user", user);

        return "admin/function/update";
    }

    @PostMapping("/admin/users/update/{maTK}")
    public String postUpdateUser(
            @PathVariable long maTK,
            @RequestParam("HoTen") String hoTen,
            @RequestParam(value = "Password", required = false) String password,
            @RequestParam("SDT") String sdt,
            @RequestParam(value = "role", required = false) String role) {

        User user = this.userRepository.findById(maTK).orElse(null);
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

    @GetMapping("/admin/users/delete/{maTK}")
    public String getDeleteUserPage(Model model,
            @PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        model.addAttribute("user", user);

        return "admin/function/delete";
    }

    @PostMapping("/admin/users/delete/{maTK}")
    public String postDeleteUser(@PathVariable long maTK) {
        this.userService.deleteUser(maTK);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/ban/{maTK}")
    public String getBanUserPage(Model model,
            @PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTK", maTK);
        return "admin/function/ban";
    }

    @PostMapping("/admin/users/ban/{maTK}")
    public String postBanUser(@PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        user.setTrangThai(UserStatus.BANNED);
        this.userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/unban/{maTK}")
    public String getUnbanUserPage(Model model,
            @PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("maTK", maTK);
        return "admin/function/unban";
    }

    @PostMapping("/admin/users/unban/{maTK}")
    public String postUnbanUser(@PathVariable long maTK) {
        User user = this.userRepository.findById(maTK).orElse(null);
        user.setTrangThai(UserStatus.ACTIVE);
        this.userService.saveUser(user);
        return "redirect:/admin/users/block";
    }

}
