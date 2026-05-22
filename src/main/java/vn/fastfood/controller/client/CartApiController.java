package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.CartUpdateRequest;
import vn.fastfood.model.CartItem;
import vn.fastfood.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getSessionCart(session));
    }

    @PutMapping("/items")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestBody CartUpdateRequest request,
            HttpSession session) {
        if (request == null || request.getSoLuong() < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Dữ liệu cập nhật giỏ hàng không hợp lệ."));
        }

        boolean updated = cartService.updateSessionQuantity(session, request.getMaMon(), request.getSoLuong());
        if (!updated) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy món trong giỏ hàng."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật số lượng trong giỏ hàng thành công."));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestParam("maMon") long maMon,
            HttpSession session) {
        boolean removed = cartService.removeSessionItem(session, maMon);

        if (!removed) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy món trong giỏ hàng."));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa món khỏi giỏ hàng thành công."));
    }
}
