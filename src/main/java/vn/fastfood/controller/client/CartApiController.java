package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

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
        private final CartService cartService;

        public CartApiController(CartService cartService) {
                this.cartService = cartService;
        }

        @GetMapping
        public ResponseEntity<List<CartItem>> getCart(
                        @RequestParam("customerId") long customerId,
                        HttpSession session) {
                return ResponseEntity.ok(cartService.getCartItems(customerId, session));
        }

        @PostMapping("/add")
        public ResponseEntity<Map<String, Object>> addCartItem(
                        @RequestParam("productID") Long productID,
                        HttpSession session) {
                CartAddResult result = cartService.addSessionCart(productID, session);

                if (!result.success()) {
                        return ResponseEntity.badRequest().body(
                                        Map.of(
                                                        "success", false,
                                                        "message", result.message()));
                }

                return ResponseEntity.ok(
                                Map.of(
                                                "success", true,
                                                "message", result.message(),
                                                "cartCount", result.cartCount()));
        }

        @PutMapping("/items")
        public ResponseEntity<Map<String, Object>> updateCartItem(
                        @RequestBody CartUpdateRequest request,
                        HttpSession session) {
                boolean result = cartService.updateQuantity(
                                request.getCustomerId(),
                                request.getMaMon(),
                                request.getSoLuong(),
                                session);

                if (result) {
                        return ResponseEntity.ok(
                                        Map.of(
                                                        "success", true,
                                                        "message", "Cap nhat so luong trong gio hang thanh cong."));
                }

                return ResponseEntity.badRequest().body(
                                Map.of(
                                                "success", false,
                                                "message", "Khong the cap nhat so luong trong gio hang."));
        }

        @DeleteMapping("/items")
        public ResponseEntity<Map<String, Object>> removeCartItem(
                        @RequestParam("customerId") long customerId,
                        @RequestParam("maMon") long maMon,
                        HttpSession session) {
                boolean result = cartService.removeItem(customerId, maMon, session);

                if (result) {
                        return ResponseEntity.ok(
                                        Map.of(
                                                        "success", true,
                                                        "message", "Xoa mon khoi gio hang thanh cong."));
                }

                return ResponseEntity.badRequest().body(
                                Map.of(
                                                "success", false,
                                                "message", "Khong the xoa mon khoi gio hang."));
        }
}
