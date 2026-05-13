package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.fastfood.repository.MonAnRepository;

@Controller
public class HomePageController {
    // home,about,contact

    private final MonAnRepository monAnRepository;

    HomePageController(MonAnRepository monAnRepository) {
        this.monAnRepository = monAnRepository;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("listMonAn", this.monAnRepository.findMonAnBestSeller(null, ""));
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    @GetMapping("/chat")
    public String getContactPage() {
        return "client/chat";
    }

    @GetMapping("/complaint")
    public String getComplaintPage() {
        return "client/complaint";
    }

}
