package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderPageController {
    @GetMapping("/client/orders")
    public String orderHistoryPage() {
        return "client/orders/order-history";
    }

    @GetMapping("/client/orders/detail")
    public String orderDetailPage() {
        return "client/orders/order-detail";
    }
}
