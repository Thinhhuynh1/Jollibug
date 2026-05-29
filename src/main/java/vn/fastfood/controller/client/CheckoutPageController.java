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

import java.util.List;

@Controller
public class CheckoutPageController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/checkout/change-address")
    public String changeAddress(HttpSession session, Model model) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        long userId = user.getMaTK();
        List<DiaChi> addresses = addressRepository.findByUserMaTKOrderByDefaultAddressDescMaDCAsc(userId);

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
}
