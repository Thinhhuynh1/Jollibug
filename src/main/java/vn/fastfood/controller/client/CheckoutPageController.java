package vn.fastfood.controller.client;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.model.CartItem;
import vn.fastfood.repository.AddressRepository;
import vn.fastfood.repository.UserRepository;

import java.util.List;

@Controller
public class CheckoutPageController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/checkout/change-address")
    public String changeAddress(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (isCartEmpty(session)) {
            redirectAttributes.addFlashAttribute("cartMessage", "Giỏ hàng đang trống. Vui lòng chọn món trước khi đặt hàng.");
            return "redirect:/cart";
        }

        long userId = user.getMaTK();
        List<DiaChi> addresses = addressRepository.findByUserMaTKOrderByDefaultAddressDescMaDCAsc(userId);

        System.out.println("[CHECKOUT ADDRESS] sessionUserId=" + session.getAttribute("userId")
                + ", sessionUser=" + describeSessionUser(session)
                + ", resolvedUserId=" + userId
                + ", email=" + user.getEmail()
                + ", addressCount=" + addresses.size());

        model.addAttribute("addresses", addresses);
        model.addAttribute("checkoutUser", user);
        model.addAttribute("currentUser", user);

        return "client/checkout/changeAddress";
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            return user;
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Number userIdNumber) {
            return userRepository.findByMaTK(userIdNumber.longValue());
        }

        return null;
    }

    private String describeSessionUser(HttpSession session) {
        if (session == null) {
            return "null";
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            return "user{maTK=" + user.getMaTK() + "}";
        }

        return String.valueOf(userObj);
    }

    private boolean isCartEmpty(HttpSession session) {
        if (session == null) {
            return true;
        }

        Object cartObj = session.getAttribute("cart");
        if (!(cartObj instanceof List<?> cart)) {
            return true;
        }

        for (Object itemObj : cart) {
            if (itemObj instanceof CartItem item && item.getSoLuong() > 0) {
                return false;
            }
        }

        return true;
    }
}
