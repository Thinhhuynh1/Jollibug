package vn.fastfood.controller.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.repository.AddressRepository;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.service.CouponService;

@Controller
public class CartController {
    // cart, checkout, orders.

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CouponService couponService;

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(HttpSession session, Model model) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        DiaChi defaultAddress = addressRepository.findByUser_MaTKAndDefaultAddressTrue(user.getMaTK());
        if (defaultAddress == null) {
            defaultAddress = addressRepository.findFirstByUser_MaTKOrderByMaDCAsc(user.getMaTK());
        }

        model.addAttribute("checkoutUser", user);
        model.addAttribute("currentUser", user);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("activeCoupons", couponService.findActiveCoupons());

        System.out.println("[CHECKOUT PAGE] userId=" + user.getMaTK()
                + ", email=" + user.getEmail());

        return "client/checkout/show";
    }

    @GetMapping("/checkout/changeAddress")
    public String oldChangeAddressRoute() {
        return "redirect:/checkout/change-address";
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "client/pay";
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
}
