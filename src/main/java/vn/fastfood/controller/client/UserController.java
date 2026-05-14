package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;
import vn.fastfood.service.EmailVerificationService;
import vn.fastfood.service.UserService;


@Controller
public class UserController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

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

            List<Object> loggedInUsers = sessionRegistry.getAllPrincipals();
            for (Object principal : loggedInUsers) {
                if (principal.equals(email)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    if (!sessions.isEmpty()) {
                    }
                }
            }

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getMaTK());
            sessionRegistry.registerNewSession(session.getId(), email);

            String roleName = user.getVaiTro().getTenVT();
            session.setAttribute("userRole", roleName);

            System.out.println("Role: " + roleName);
            if ("ADMIN".equals(roleName)) {
                return "redirect:/admin";
            } else if ("MANAGER".equals(roleName)) {
                return "redirect:/manager";
            } else if ("STAFF".equals(roleName)) {
                return "redirect:/staff/orders"; // sau khi dang nhap thi se chuyen sang trang staff order
            }

            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "client/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // Hủy dữ liệu trong session
        User user = (User) session.getAttribute("user");
        String TenVT = (user != null ? user.getVaiTro().getTenVT() : "");

        if (session != null) {
            session.invalidate();
        }

        if (TenVT.equals("CLIENT"))
            return "redirect:/";
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "client/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
        @RequestParam("email") String email, 
        HttpSession session, 
        Model model) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("Email không tồn tại");
            }
            String code = String.format("%06d", new java.util.Random().nextInt(1000000));
            session.removeAttribute("otpVerified");
            session.setAttribute("otp", code);
            session.setAttribute("otpSentTime", System.currentTimeMillis());
            session.setAttribute("email", email);
            emailVerificationService.sendMail(email, code); 
            return "redirect:/verify";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "client/forgot-password";
        }
    }

    @PostMapping("/verify")
    public String verifyUser(
        @RequestParam("verify-code") 
        String code, 
        Model model, 
        HttpSession session) {
        try {
            String sessionOtp = (String) session.getAttribute("otp");
            long currentTime = System.currentTimeMillis();
            long sentTime = (long) session.getAttribute("otpSentTime");
            long expiredTime = 2 * 60 * 1000;

            if (sessionOtp == null) {
                model.addAttribute("message", "Mã không tồn tại");
                return "client/verify";
            }
            if (currentTime - sentTime > expiredTime) {
                session.removeAttribute("otp");
                session.removeAttribute("otpSentTime");
                session.removeAttribute("otpVerified");
                session.removeAttribute("email");
                model.addAttribute("message", "Mã OTP đã hết hạn");
                return "client/verify";
            }
            if (sessionOtp.equals(code)) {
                session.setAttribute("otpVerified", true);
                return "redirect:/new-password";
            } 
            else {
                model.addAttribute("message", "Mã xác thực sai!");
                return "client/verify";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "client/verify";
        }
    }

    @GetMapping("/verify")
    public String verifyPage() {
        return "client/verify";
    }

    @PostMapping("/verify/resend")
    public String resendVerificationCode(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phiên xác thực. Vui lòng thử lại.");
            return "redirect:/forgot-password";
        }

        String code = String.format("%06d", new java.util.Random().nextInt(1000000));
        session.setAttribute("otp", code);
        session.setAttribute("otpSentTime", System.currentTimeMillis());
        session.removeAttribute("otpVerified");
        emailVerificationService.sendMail(email, code);
        redirectAttributes.addFlashAttribute("message", "Mã xác thực mới đã được gửi đến email của bạn.");
        return "redirect:/verify";
    }

    @GetMapping("/new-password")
    public String newPasswordPage(HttpSession session) {
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("email");
        if (!Boolean.TRUE.equals(otpVerified) || email == null) {
            return "redirect:/forgot-password";
        }
        return "client/new-password";
    }

    @PostMapping("/new-password")
    public String changePassword(
            @RequestParam("newPassword") String newPass,
            @RequestParam("confirmPassword") String confirmPass,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws Exception {
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("email");
        if (!Boolean.TRUE.equals(otpVerified) || email == null) {
            redirectAttributes.addFlashAttribute("error", "Session đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/forgot-password";
        }
        if (!newPass.equals(confirmPass)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/new-password";
        }
        try {
            userService.changePasswordByEmail(email, newPass);
            session.removeAttribute("otp");
            session.removeAttribute("otpSentTime");
            session.removeAttribute("otpVerified");
            session.removeAttribute("email");
            redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }


}



