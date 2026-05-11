package vn.fastfood.controller.staff;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderController {

    private final OrderService orderService = new OrderService();

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForStaff(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        List<Order> orders = orderService.getOrdersForStaff(status, keyword, fromDate, toDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetailForStaff(
            @PathVariable("orderId") long orderId
    ) {
        Order order = orderService.getOrderByIdForStaff(orderId);

        if (order == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "success", false,
                            "message", "Không tìm thấy đơn hàng."
                    )
            );
        }

        List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);

        OrderDetailResponse response = new OrderDetailResponse(order, items);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
        public ResponseEntity<Map<String, Object>> updateOrderStatus(
                @PathVariable("orderId") long orderId,
                @RequestParam("staffId") long staffId,
                @RequestParam("status") String status,
                @RequestParam(value = "cancelReason", required = false) String cancelReason
        ) {
        boolean result = orderService.updateOrderStatusByStaff(orderId, staffId, status, cancelReason);

        if (result) {
                return ResponseEntity.ok(
                        Map.of(
                                "success", true,
                                "message", "Cập nhật trạng thái đơn hàng thành công."
                        )
                );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể cập nhật trạng thái đơn hàng. Vui lòng kiểm tra luồng trạng thái."
                )
        );
        }
}