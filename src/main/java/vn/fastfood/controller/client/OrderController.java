package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.service.JpaOrderService;
import vn.fastfood.service.ReviewService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final JpaOrderService jpaOrderService;
    private final ReviewService reviewService;

    public OrderController(JpaOrderService jpaOrderService, ReviewService reviewService) {
        this.jpaOrderService = jpaOrderService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByCustomerId(
            @RequestParam("customerId") long customerId) {
        List<Order> orders = jpaOrderService.getOrdersByCustomerId(customerId);
        orders.forEach(reviewService::enrichOrderReviewMeta);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(
            @PathVariable("orderId") long orderId,
            @RequestParam("customerId") long customerId) {
        Order order = jpaOrderService.getOrderById(orderId, customerId);

        if (order == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "success", false,
                            "message", "Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này."));
        }

        reviewService.enrichOrderReviewMeta(order);

        List<OrderItem> items = jpaOrderService.getOrderItemsByOrderId(orderId);
        List<ReviewResponse> reviews = reviewService.listByOrderAndCustomer(orderId, customerId);
        OrderDetailResponse response = new OrderDetailResponse(order, items, reviews);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable("orderId") long orderId,
            @RequestParam("customerId") long customerId) {
        Order order = jpaOrderService.getOrderById(orderId, customerId);
        if (order == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Không thể hủy đơn hàng này."));
        }

        String status = normalizeStatus(order.getTrangThaiDon());
        String nextStatus = null;
        if ("PENDING".equals(status)) {
            nextStatus = "CANCELLED";
        } else if ("CONFIRMED".equals(status)) {
            nextStatus = "CANCEL_REQUESTED";
        }

        if (nextStatus == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Không thể hủy đơn hàng này."));
        }

        jpaOrderService.updateStatus(orderId, nextStatus);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Cập nhật hủy/ yêu cầu hủy đơn thành công."));
    }

    @PostMapping("/{orderId}/received")
    public ResponseEntity<Map<String, Object>> confirmReceived(
            @PathVariable("orderId") long orderId,
            @RequestParam("customerId") long customerId) {
        Order order = jpaOrderService.getOrderById(orderId, customerId);
        if (order == null || !"SHIPPING".equals(normalizeStatus(order.getTrangThaiDon()))) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Không thể xác nhận đã nhận hàng cho đơn này."));
        }

        jpaOrderService.updateStatus(orderId, "DELIVERED");
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Xác nhận đã nhận hàng thành công."));
    }

    @GetMapping("/{orderId}/can-review")
    public ResponseEntity<Map<String, Object>> canReviewOrder(
            @PathVariable("orderId") long orderId,
            @RequestParam("customerId") long customerId) {
        boolean canReview = reviewService.canReviewOrder(orderId, customerId);
        return ResponseEntity.ok(Map.of("success", true, "canReview", canReview));
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toUpperCase();
    }
}
