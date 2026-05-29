package vn.fastfood.controller.manager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.config.PhantomReadDemoSettings;
import vn.fastfood.service.OrderService;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/statistics")
public class ManagerPhantomReadDemoController {

    private final OrderService orderService = new OrderService();

    /** Chạy demo Phantom Read: đọc COUNT 2 lần, sleep 5s ở giữa */
    @GetMapping("/phantom-read/demo")
    public ResponseEntity<?> runPhantomReadDemo(
            @RequestParam(value = "isolation", defaultValue = "READ_COMMITTED") String isolation,
            @RequestParam(value = "delayMs",   defaultValue = "5000")           long   delayMs
    ) {
        try {
            Map<String, Object> result =
                    orderService.countOrderStatsTwiceForPhantomReadDemo(isolation, delayMs);
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Lỗi cơ sở dữ liệu khi chạy demo Phantom Read.")
            );
        }
    }

    /** Lấy chế độ hiện tại (SAFE / UNSAFE) */
    @GetMapping("/phantom-read/mode")
    public ResponseEntity<Map<String, Object>> getPhantomReadMode() {
        return ResponseEntity.ok(
                Map.of("success", true, "mode", PhantomReadDemoSettings.getMode())
        );
    }

    /** Đổi chế độ (SAFE / UNSAFE) */
    @PostMapping("/phantom-read/mode")
    public ResponseEntity<Map<String, Object>> setPhantomReadMode(
            @RequestParam("mode") String mode
    ) {
        String normalized = PhantomReadDemoSettings.setMode(mode);
        return ResponseEntity.ok(
                Map.of("success", true, "mode", normalized)
        );
    }
}
