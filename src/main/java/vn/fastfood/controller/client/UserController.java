package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    // login,register,forgot-password,profile
    @GetMapping("/login")
    public String getLoginPage() {
        return "client/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "client/register";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "client/forgot-password";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "client/profile";
    }

    @GetMapping("/address")
    public String getAddressPage() {
        return "client/address";
    }

}
