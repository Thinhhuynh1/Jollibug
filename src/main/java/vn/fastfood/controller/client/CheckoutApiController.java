package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.service.CheckoutService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutApiController {
    private final CheckoutService checkoutService = new CheckoutService();

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.checkout(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}