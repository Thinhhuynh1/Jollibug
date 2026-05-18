package vn.fastfood.controller.client;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.ReorderResponse;
import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.dto.OrderStatusHistoryResponse;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.Order;
import vn.fastfood.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService = new OrderService();

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByCustomerId(
        @RequestParam("customerId") long customerId
    ) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId
    ) {
        Order order = orderService.getOrderById(orderId, customerId);

        if(order == null)
        {
            return ResponseEntity.status(404).body(
                Map.of(
                    "success", false,
                    "message", "Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này."
                )
            );
        }
        List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
        OrderDetailResponse response = new OrderDetailResponse(order, items);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId,
        @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        boolean result = orderService.requestCancelOrder(orderId, customerId, cancelReason);
        
        if (result) {
            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message", "Cập nhật hủy/ yêu cầu hủy đơn thành công."
                )
            );
        }

        return ResponseEntity.badRequest().body(
            Map.of(
                "success", false,
                "message", "Không thể hủy đơn hàng này."
            )
        );
    }

    @PostMapping("/{orderId}/received")
    public ResponseEntity<Map<String, Object>> confirmReceived(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId
    ) {
        boolean result = orderService.confirmReceived(orderId, customerId);

        if (result) {
            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message", "Xác nhận đã nhận hàng thành công."
                )
            );
        }
        return ResponseEntity.badRequest().body(
            Map.of(
                "success", false,
                "message", "Không thể xác nhận đã nhận hàng cho đơn này."
            )
        );
    }

    @GetMapping("/{orderId}/status-history")
    public ResponseEntity<?> getOrderStatusHistory(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId
    ) {
        List<OrderStatusHistoryResponse> history = orderService.getOrderStatusHistoryForCustomer(orderId, customerId);

        if (history == null) {
            return ResponseEntity.status(404).body(
                Map.of(
                    "success", false,
                    "message", "Order not found for this customer."
                )
            );
        }

        return ResponseEntity.ok(history);
    }

    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<ReorderResponse> reorder(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId,
        HttpSession session
    ) {
        ReorderResponse response = orderService.prepareReorderCheckout(orderId, customerId, session);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{orderId}/can-review")
    public ResponseEntity<Map<String, Object>> canReviewOrder(
        @PathVariable("orderId") long orderId,
        @RequestParam("customerId") long customerId
    ) {
        boolean canReview = orderService.canReviewOrder(orderId, customerId);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "canReview", canReview
            )
        );
    }
}
