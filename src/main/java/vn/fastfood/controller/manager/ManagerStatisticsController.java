package vn.fastfood.controller.manager;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.service.ThongKeService;

@Controller
public class ManagerStatisticsController {

    private final ThongKeService thongKeService;

    public ManagerStatisticsController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    // Trang thống kê doanh thu
    @GetMapping("/manager/statistics/revenue")
    public String getRevenuePage(
            @RequestParam(value = "period", required = false, defaultValue = "month") String period,
            Model model) {
        Map<String, Object> stats = thongKeService.getRevenueStats(period);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedPeriod", period);
        return "manager/statistics/revenue";
    }

    // Trang thống kê đơn hàng
    @GetMapping("/manager/statistics/orders")
    public String getOrdersPage(
            @RequestParam(value = "period", required = false, defaultValue = "month") String period,
            Model model) {
        Map<String, Object> stats = thongKeService.getOrderStats(period);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedPeriod", period);
        return "manager/statistics/orders";
    }

    // Trang thống kê khách hàng
    @GetMapping("/manager/statistics/customers")
    public String getCustomersPage(
            @RequestParam(value = "period", required = false, defaultValue = "month") String period,
            Model model) {
        Map<String, Object> stats = thongKeService.getCustomerStats(period);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedPeriod", period);
        return "manager/statistics/customers";
    }
}
