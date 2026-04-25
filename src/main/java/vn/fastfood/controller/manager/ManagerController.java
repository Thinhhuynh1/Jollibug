package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerController {
    @GetMapping("/manager")
    public String getDashBoardPage() {
        return "manager/dashboard";
    }

}
