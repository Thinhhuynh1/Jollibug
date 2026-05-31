package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.repository.AddressRepository;

@Controller
public class CartController {

    private final AddressRepository addressRepository;

    CartController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "client/cart";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đặt hàng.");
            return "redirect:/login";
        }

        DiaChi defaultAddress = addressRepository.findByUser_MaTKAndDefaultAddressTrue(user.getMaTK());
        if (defaultAddress == null) {
            defaultAddress = addressRepository.findFirstByUser_MaTKOrderByMaDCAsc(user.getMaTK());
        }

        model.addAttribute("user", user);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("defaultMaDC", defaultAddress != null ? defaultAddress.getMaDC() : "");
        return "client/checkout/show";
    }

    @GetMapping("/checkout/changeAddress")
    public String getCheckoutAddress(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("listAddress", addressRepository.findByUser_MaTKOrderByMaDCAsc(user.getMaTK()));
        return "client/checkout/changeAddress";
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "client/pay";
    }
}
