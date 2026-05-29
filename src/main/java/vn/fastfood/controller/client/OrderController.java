package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.dto.OrderStatusHistoryResponse;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.CartService;
import vn.fastfood.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService = new OrderService();

    @Autowired
    private CartService cartService;

    @Autowired
    private MonAnRepository monAnRepository;

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByMaKH(@RequestParam("maKH") long maKH) {
        return ResponseEntity.ok(orderService.getOrdersByMaKH(maKH));
    }

    @GetMapping("/{maDH}")
    public ResponseEntity<?> getOrderDetail(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        Order donHang = orderService.getOrderByMaDH(maDH, maKH);
        if (donHang == null) {
            return error(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
        }

        List<OrderItem> chiTietDonHang = orderService.getOrderItemsByMaDH(maDH);
        return ResponseEntity.ok(new OrderDetailResponse(donHang, chiTietDonHang));
    }

    @PostMapping("/{maDH}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        if (!orderService.requestCancelOrder(maDH, maKH)) {
            return error(HttpStatus.BAD_REQUEST, "Không thể hủy đơn hàng này.");
        }

        return ok("Cập nhật hủy/yêu cầu hủy đơn thành công.");
    }

    @PostMapping("/{maDH}/received")
    public ResponseEntity<Map<String, Object>> confirmReceived(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        if (!orderService.confirmReceived(maDH, maKH)) {
            return error(HttpStatus.BAD_REQUEST, "Không thể xác nhận đã nhận hàng cho đơn này.");
        }

        return ok("Xác nhận đã nhận hàng thành công.");
    }

    @GetMapping("/{maDH}/status-history")
    public ResponseEntity<?> getOrderStatusHistory(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        List<OrderStatusHistoryResponse> history = orderService.getOrderStatusHistoryForCustomer(maDH, maKH);
        if (history == null) {
            return error(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng cho khách hàng này.");
        }

        return ResponseEntity.ok(history);
    }

    @PostMapping("/{maDH}/reorder")
    public ResponseEntity<Map<String, Object>> reorder(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH,
            HttpSession session) {
        Order donHang = orderService.getOrderByMaDH(maDH, maKH);
        if (donHang == null) {
            return error(HttpStatus.BAD_REQUEST, "Không thể đặt lại đơn hàng này.");
        }

        List<OrderItem> chiTietDonHang = orderService.getOrderItemsByMaDH(maDH);
        int soMonDaThem = addItemsToSessionCart(chiTietDonHang, session);
        if (soMonDaThem == 0) {
            return error(HttpStatus.BAD_REQUEST, "Không thể đặt lại đơn hàng này.");
        }

        return ok("Đã thêm lại các món trong đơn vào giỏ hàng.");
    }

    @GetMapping("/{maDH}/can-review")
    public ResponseEntity<Map<String, Object>> canReviewOrder(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "canReview", orderService.canReviewOrder(maDH, maKH)
        ));
    }

    private int addItemsToSessionCart(List<OrderItem> chiTietDonHang, HttpSession session) {
        int soMonDaThem = 0;

        for (OrderItem chiTiet : chiTietDonHang) {
            MonAn monAn = monAnRepository.findProduct(chiTiet.getMaMon());
            if (monAn == null) {
                continue;
            }

            cartService.addSessionCart(monAn, chiTiet.getSoLuong(), session);
            soMonDaThem++;
        }

        return soMonDaThem;
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
