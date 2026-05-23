package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.service.CartService;
import vn.fastfood.dto.CartUpdateRequest;
import vn.fastfood.model.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        return ResponseEntity.ok(getSessionCart(session));
    }

    @PutMapping("/items")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestBody CartUpdateRequest request,
            HttpSession session
    ) {
        if (request == null || request.getSoLuong() < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Dữ liệu cập nhật giỏ hàng không hợp lệ."
            ));
        }

        List<CartItem> cart = getSessionCart(session);

        for (CartItem item : cart) {
            if (item.getMaMon() == request.getMaMon()) {
                int newQuantity = request.getSoLuong();

                item.setSoLuong(newQuantity);

                BigDecimal donGia = item.getDonGia() == null ? BigDecimal.ZERO : item.getDonGia();
                item.setThanhTien(donGia.multiply(BigDecimal.valueOf(newQuantity)));

                session.setAttribute("cart", cart);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Cập nhật số lượng trong giỏ hàng thành công."
                ));
            }
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Không tìm thấy món trong giỏ hàng."
        ));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestParam("maMon") long maMon,
            HttpSession session
    ) {
        List<CartItem> cart = getSessionCart(session);
        boolean removed = false;

        Iterator<CartItem> iterator = cart.iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();

            if (item.getMaMon() == maMon) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        session.setAttribute("cart", cart);

        if (!removed) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy món trong giỏ hàng."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa món khỏi giỏ hàng thành công."
        ));
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getSessionCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj instanceof List<?>) {
            return (List<CartItem>) cartObj;
        }

        List<CartItem> cart = new ArrayList<>();
        session.setAttribute("cart", cart);
        return cart;
    }
}