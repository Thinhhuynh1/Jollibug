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
        return ResponseEntity.ok(orderService.getOrdersForStaff(status, keyword, fromDate, toDate));
    }

    @GetMapping("/{maDH}")
    public ResponseEntity<?> getOrderDetailForStaff(@PathVariable("maDH") long maDH) {
        Order order = orderService.getOrderByIdForStaff(maDH);

        if (order == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Không tìm thấy đơn hàng."));
        }

        List<OrderItem> items = orderService.getOrderItemsByMaDH(maDH);
        return ResponseEntity.ok(new OrderDetailResponse(order, items));
    }

    @PutMapping("/{maDH}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable("maDH") long maDH,
            @RequestParam("staffId") long staffId,
            @RequestParam("status") String status,
            @RequestParam(value = "cancelReason", required = false) String cancelReason
    ) {
        boolean result = orderService.updateOrderStatusByStaff(maDH, staffId, status, cancelReason);

        if (result) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật trạng thái đơn hàng thành công."));
        }

        return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Không thể cập nhật trạng thái đơn hàng. Vui lòng kiểm tra luồng trạng thái."));
    }
}
