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

    @GetMapping("/reset-password")
    public String getResetPasswordPage() {
        return "client/reset-password";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "client/profile";
    }

    @GetMapping("/address")
    public String getAddressPage() {
        return "client/address/show";
    }

    @GetMapping("/address/create")
    public String getAddressCreate() {
        return "client/address/create";
    }

    @GetMapping("/address/update")
    public String getAddressUpdate() {
        return "client/address/update";
    }

    @GetMapping("/address/delete")
    public String getAddressDelete() {
        return "client/address/delete";
    }

}
