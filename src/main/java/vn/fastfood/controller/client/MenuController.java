package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.fastfood.repository.MonAnRepository;

@Controller
public class MenuController {
    private final MonAnRepository monAnRepository;

    public MenuController(MonAnRepository monAnRepository) {
        this.monAnRepository = monAnRepository;
    }

    @GetMapping("/menu")
    public String getMenuPage(Model model) {
        model.addAttribute("listMonAn", this.monAnRepository.findAll());
        return "client/menu";
    }
}
