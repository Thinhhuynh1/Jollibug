package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.CartUpdateRequest;
import vn.fastfood.model.CartItem;
import vn.fastfood.service.CartService;
import vn.fastfood.service.CartService.CartAddResult;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getSessionCart(session));
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addCartItem(
            @RequestParam("productID") long productID,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session) {
        CartAddResult result = cartService.addSessionCart(productID, quantity, session);
        if (!result.success()) {
            return error(HttpStatus.BAD_REQUEST, result.message());
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result.message(),
                "cartCount", result.cartCount()
        ));
    }

    @PutMapping("/items")
    public ResponseEntity<Map<String, Object>> updateCartItem(@RequestBody CartUpdateRequest request, HttpSession session) {
        if (request == null || request.getSoLuong() < 1) {
            return error(HttpStatus.BAD_REQUEST, "Dữ liệu cập nhật giỏ hàng không hợp lệ");
        }

        boolean updated = cartService.updateSessionQuantity(session, request.getMaMon(), request.getSoLuong());
        if (!updated) {
            return error(HttpStatus.BAD_REQUEST, "Không tìm thấy món trong giỏ hàng");
        }

        return ok("Cập nhật số lượng trong giỏ hàng thành công");
    }

    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestParam("maMon") long maMon,
            HttpSession session) {
        boolean removed = cartService.removeSessionItem(session, maMon);
        if (!removed) {
            return error(HttpStatus.BAD_REQUEST, "Không tìm thấy món trong giỏ hàng");
        }

        return ok("Xóa món khỏi giỏ hàng thành công");
    }

    private ResponseEntity<Map<String, Object>> ok(String message) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message
        ));
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "message", message
        ));
    }
}
