package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;
import vn.fastfood.service.UserService;


@Controller
public class ProfileController {
    private final UserService userService;

    ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage() {
        return "client/reset-password";
    }

@PostMapping("/reset-password")
public String resetPassword(
    @RequestParam("currentPassword") String currentPass, 
    @RequestParam("newPassword") String newPass, 
    @RequestParam("confirmPassword") String confirmPass,
    RedirectAttributes redirectAttributes,
    HttpSession session) throws Exception {
    if (!newPass.equals(confirmPass)) {
        redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
        return "redirect:/reset-password";
    }
    try {
        userService.resetPassword(session,currentPass, newPass);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        return "redirect:/login";
    } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/profile";
    }
}
    

    @GetMapping("/profile")
    public String getProfilePage() {
        return "client/profile";
    }

    @PostMapping("/profile/update") 
    public String updateProfile(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
        // 1. Lấy ID của user đang đăng nhập (Giả sử bạn lưu ID lúc login với tên "loggedUserId")
        Long id = ((User) session.getAttribute("user")).getMaTK();

        if (id != null) {
            // 2. Gọi DB lấy toàn bộ thông tin gốc của User này ra (để giữ nguyên Password, Role...)
            User exUser = userService.getUserByMaTK(id);
            // 3. Chỉ cập nhật những trường mà Form gửi lên
            exUser.setHoTen(user.getHoTen());
            exUser.setSdt(user.getSdt());
            // existingUser.setAddress(user.getAddress());
            // KHÔNG cập nhật email và password ở đây

            // 4. Lưu lại vào Database
            userService.saveUser(exUser);
            session.setAttribute("user", exUser);

            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật tài khoản thành công");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui lòng đăng nhập lại");
        }

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMsg", "Cập nhật tài khoản không thành công");
    }

        return "redirect:/profile"; 
    }

    @GetMapping("/orders")
    public String getOrderPage() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/pending")
    public String getOrderPending() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/confirmed")
    public String getOrderConfirmed() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/shipping")
    public String getOrderShipping() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/delivered")
    public String getOrderDelivered() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/cancelled")
    public String getOrderCancelled() {
        return "client/orders/order-history";
    }

    @GetMapping("/orders/cancel")
    public String getOrderCancel() {
        return "client/orders/cancel";
    }

    @GetMapping("/orders/detail")
    public String getOrderDetail() {
        return "client/orders/order-detail";
    }

    @GetMapping("/orders/reviews")
    public String getOrderReviews() {
        return "client/orders/reviews";
    }

    @GetMapping("/orders/reviews/create")
    public String getReviewsCreate() {
        return "client/orders/reviewsCreate";
    }

    @GetMapping("/orders/reviews/view")
    public String getReviewsView() {
        return "client/orders/reviewsView";
    }

    @GetMapping("/orders/reviews/update")
    public String getReviewsUpdate() {
        return "client/orders/reviewsUpdate";
    }

    @GetMapping("/orders/reviews/delete")
    public String getReviewsDelete() {
        return "client/orders/reviewsDelete";
    }
}
