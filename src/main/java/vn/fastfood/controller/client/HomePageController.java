package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
    // home,about,contact

    @GetMapping("/")
    public String getHomePage() {
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "client/contact";
    }

    @GetMapping("/menu")
    public String getMenuPage() {
        return "client/menu";
    }

}
