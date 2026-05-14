package vn.fastfood.controller.manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.fastfood.service.ThongKeService;

@Controller
public class ManagerController {

    private final ThongKeService thongKeService;

    public ManagerController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    @GetMapping("/manager")
    public String getDashBoardPage(Model model) {
        model.addAttribute("revenueStats", thongKeService.getRevenueStats("month"));
        model.addAttribute("orderStats", thongKeService.getOrderStats("month"));
        model.addAttribute("customerStats", thongKeService.getCustomerStats("month"));
        return "manager/dashboard";
    }

    @GetMapping("/manager/dashboard/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> revenueStats = thongKeService.getRevenueStats("month");
        Map<String, Object> orderStats = thongKeService.getOrderStats("month");
        Map<String, Object> customerStats = thongKeService.getCustomerStats("month");

        Map<String, Object> payload = new HashMap<>();
        payload.put("revenue", revenueStats);
        payload.put("orders", orderStats);
        payload.put("customers", customerStats);

        return ResponseEntity.ok(payload);
    }
}
