package vn.fastfood.controller.staff;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.service.JpaOrderService;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderController {

    private final JpaOrderService jpaOrderService;

    public StaffOrderController(JpaOrderService jpaOrderService) {
        this.jpaOrderService = jpaOrderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForStaff(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        return ResponseEntity.ok(jpaOrderService.getOrdersForStaff(status, keyword, fromDate, toDate));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetailForStaff(@PathVariable("orderId") long orderId) {
        Order order = jpaOrderService.getOrderByIdForStaff(orderId);

        if (order == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "success", false,
                            "message", "Không tìm thấy đơn hàng."));
        }

        List<OrderItem> items = jpaOrderService.getOrderItemsByOrderId(orderId);
        OrderDetailResponse response = new OrderDetailResponse(order, items);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable("orderId") long orderId,
            @RequestParam("staffId") long staffId,
            @RequestParam("status") String status,
            @RequestParam(value = "cancelReason", required = false) String cancelReason) {
        String currentStatus = normalizeStatus(jpaOrderService.getCurrentStatus(orderId));
        String nextStatus = normalizeStatus(status);

        if (!isValidStaffTransition(currentStatus, nextStatus)) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Không thể cập nhật trạng thái đơn hàng. Vui lòng kiểm tra luồng trạng thái."));
        }

        boolean updated = jpaOrderService.updateStatus(orderId, nextStatus);
        if (updated) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Cập nhật trạng thái đơn hàng thành công."));
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể cập nhật trạng thái đơn hàng."));
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toUpperCase();
    }

    private boolean isValidStaffTransition(String current, String next) {
        if ("PENDING".equals(current) && "CONFIRMED".equals(next)) {
            return true;
        }
        if ("PENDING".equals(current) && "CANCELLED".equals(next)) {
            return true;
        }
        if ("CONFIRMED".equals(current) && "SHIPPING".equals(next)) {
            return true;
        }
        if ("CONFIRMED".equals(current) && "CANCELLED".equals(next)) {
            return true;
        }
        if ("SHIPPING".equals(current) && "DELIVERED".equals(next)) {
            return true;
        }
        if ("CANCEL_REQUESTED".equals(current) && "CANCELLED".equals(next)) {
            return true;
        }
        if ("CANCEL_REQUESTED".equals(current) && "CONFIRMED".equals(next)) {
            return true;
        }
        return false;
    }
}
