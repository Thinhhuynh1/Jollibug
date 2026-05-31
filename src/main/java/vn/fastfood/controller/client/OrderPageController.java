package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderPageController {

    @GetMapping("/client/orders")
    public String orderHistoryPage() {
        return "redirect:/orders";
    }

    @GetMapping("/client/orders/detail")
    public String orderDetailPage(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            return "redirect:/orders/detail?" + query;
        }
        return "redirect:/orders/detail";
    }
}