package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
    // cart,checkout,orders.

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage() {
        return "client/checkout";
    }

}
