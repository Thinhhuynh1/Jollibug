package vn.fastfood.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;
import vn.fastfood.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // login,register,forgot-password,profile
    @GetMapping("/login")
    public String getLoginPage() {
        return "client/login";
    }

    // Phần đăng kí
    // Model tạo 1 cái thùng chứa dữ liệu trống để khi nhập vào form trong jsp sẽ
    // lưu vào đây
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "client/register";
    }

    // Gọi user dựa trên model tạo ở trên, gọi lại model dó để add thêm thuộc tính
    // nếu lỗi
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "client/register";
        }
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email, @RequestParam("password") String password,
            Model model, HttpSession session) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getMaTK());
            session.setAttribute("userRole", String.valueOf(user.getVaiTro().getMaVT()));
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "client/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // Hủy dữ liệu trong session
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "client/forgot-password";
    }
}
