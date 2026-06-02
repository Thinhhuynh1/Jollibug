package vn.fastfood.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
