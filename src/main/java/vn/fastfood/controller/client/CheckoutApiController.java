package vn.fastfood.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.model.CheckoutCartItem;
import vn.fastfood.service.CheckoutService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutApiController {
    private final CheckoutService checkoutService;

    public CheckoutApiController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam("customerId") long customerId,
            HttpSession session) {
        List<CheckoutCartItem> items = checkoutService.getCheckoutItems(customerId, session);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CheckoutCartItem item : items) {
            if (item.getThanhTien() != null) {
                subtotal = subtotal.add(item.getThanhTien());
            } else if (item.getDonGia() != null) {
                subtotal = subtotal.add(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        payload.put("total", items.size());
        payload.put("subtotal", subtotal);
        payload.put("data", items);
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request,
            HttpSession session) {
        CheckoutResponse response = checkoutService.checkout(request, session);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}
