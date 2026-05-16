package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.repository.UserRepository;

@Controller
public class CartController {
    private final UserRepository userRepository;

    public CartController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(Model model, HttpSession session) {
        Long maTK = (Long) session.getAttribute("userId");
        if (maTK == null) {
            return "redirect:/login";
        }

        User user = this.userRepository.findByMaTK(maTK);
        if (user == null) {
            return "redirect:/login";
        }

        DiaChi defaultAddress = null;
        List<DiaChi> listAddress = user.getDiaChi();
        if (listAddress != null) {
            for (DiaChi address : listAddress) {
                if (address != null && address.isDefaultAddress()) {
                    defaultAddress = address;
                    break;
                }
            }
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("defaultAddress", defaultAddress);
        return "client/checkout/show";
    }

    @GetMapping("/checkout/changeAddress")
    public String getCheckoutAddress(Model model, HttpSession session) {
        Long maTK = (Long) session.getAttribute("userId");
        if (maTK == null) {
            return "redirect:/login";
        }

        User user = this.userRepository.findByMaTK(maTK);
        if (user == null) {
            return "redirect:/login";
        }

        List<DiaChi> listAddress = user.getDiaChi();
        model.addAttribute("listAddress", listAddress);
        return "client/checkout/changeAddress";
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "client/pay";
    }

}
