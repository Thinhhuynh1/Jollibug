package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.CartUpdateRequest;
import vn.fastfood.model.CartItem;
import vn.fastfood.service.CartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    private final CartService cartService = new CartService();

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(
            @RequestParam("customerId") long customerId
    ) {
        return ResponseEntity.ok(cartService.getCartItems(customerId));
    }

    @PutMapping("/items")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestBody CartUpdateRequest request
    ) {
        boolean result = cartService.updateQuantity(
                request.getCustomerId(),
                request.getMaMon(),
                request.getSoLuong()
        );

        if (result) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Cập nhật số lượng trong giỏ hàng thành công."
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể cập nhật số lượng trong giỏ hàng."
                )
        );
    }

    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestParam("customerId") long customerId,
            @RequestParam("maMon") long maMon
    ) {
        boolean result = cartService.removeItem(customerId, maMon);

        if (result) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Xóa món khỏi giỏ hàng thành công."
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể xóa món khỏi giỏ hàng."
                )
        );
    }
}