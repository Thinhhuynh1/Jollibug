package vn.fastfood.controller.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fastfood.dto.ReorderResponse;
import vn.fastfood.entity.User;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.service.OrderService;

import java.util.List;

@Controller
public class CartController {
    // cart, checkout, orders.

    @Autowired
    private UserRepository userRepository;

    private final OrderService orderService = new OrderService();

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(
            @RequestParam(value = "reorderOrderId", required = false) Long reorderOrderId,
            HttpSession session,
            Model model
    ) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (reorderOrderId != null && reorderOrderId > 0) {
            ReorderResponse reorderResponse = orderService.prepareReorderCheckout(
                    reorderOrderId,
                    user.getMaTK(),
                    session
            );

            model.addAttribute("reorderMessage", reorderResponse.getMessage());
            model.addAttribute("reorderSkippedItems", reorderResponse.getSkippedItems());

            if (!reorderResponse.isSuccess()) {
                model.addAttribute("reorderError", reorderResponse.getMessage());
            }
        }

        if (isCartEmpty(session)) {
            return "redirect:/cart?checkoutEmpty=true";
        }

        model.addAttribute("checkoutUser", user);

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

    private boolean isCartEmpty(HttpSession session) {
        if (session == null) {
            return true;
        }

        Object cartObj = session.getAttribute("cart");

        if (!(cartObj instanceof List<?> cart)) {
            return true;
        }

        return cart.stream().noneMatch(item -> {
            if (item instanceof vn.fastfood.model.CartItem cartItem) {
                return cartItem.getSoLuong() > 0;
            }

            return false;
        });
    }
}
