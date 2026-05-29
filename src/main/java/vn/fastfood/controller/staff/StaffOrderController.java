package vn.fastfood.controller.staff;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.OrderDetailResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.service.OrderService;

import java.sql.SQLException;
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

    @GetMapping("/concurrency-mode")
    public ResponseEntity<Map<String, Object>> getConcurrencyMode() {
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "mode", vn.fastfood.config.OrderConcurrencyDemoSettings.getMode()
                )
        );
    }

    @PostMapping("/concurrency-mode")
    public ResponseEntity<Map<String, Object>> setConcurrencyMode(
            @RequestParam("mode") String mode
    ) {
        String normalizedMode = vn.fastfood.config.OrderConcurrencyDemoSettings.setMode(mode);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "mode", normalizedMode
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetailForStaff(
            @PathVariable("orderId") long orderId
    ) {
        String mode = vn.fastfood.config.OrderConcurrencyDemoSettings.getMode();

        try {
            Map<String, Object> demoResult = orderService.getOrderByIdForStaffWithDemo(orderId, mode, 5000L);
            Object orderObj = demoResult.get("order");
            Order order = orderObj instanceof Order ? (Order) orderObj : null;

            if (order == null || "NOT_FOUND".equals(demoResult.get("firstStatus"))) {
                return ResponseEntity.status(404).body(
                        Map.of(
                                "success", false,
                                "message", "Không tìm thấy đơn hàng hoặc đơn chưa được khởi tạo."
                        )
                );
            }

            List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "order", order,
                    "orderItems", items,
                    "demoMode", mode,
                    "firstStatus", demoResult.get("firstStatus"),
                    "secondStatus", demoResult.get("secondStatus"),
                    "changed", demoResult.get("changed"),
                    "isolation", demoResult.get("isolation")
            ));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", "Lỗi cơ sở dữ liệu khi demo Non-repeatable Read."
                    )
            );
        }
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
