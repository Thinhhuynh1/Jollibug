package vn.fastfood.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.fastfood.service.GioHangService;

@Controller
public class CartController {
    // cart,checkout,orders.

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage() {
        return "client/checkout/show";
    }

    @GetMapping("/checkout/changeAddress")
    public String getCheckoutAddress() {
        return "client/checkout/changeAddress";
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "client/pay";
    }
    @Autowired
    private GioHangService gioHangService;

    @PostMapping("/cart/add/{id}")
    public String addCart(
        @PathVariable Long id,
        @RequestParam(name = "qty", defaultValue = "1") int qty,
        RedirectAttributes redirectAttributes) {

    gioHangService.addToCart(id, qty);
    redirectAttributes.addFlashAttribute("success", "Thêm thành công");
    return "redirect:/menu";
    }
}
