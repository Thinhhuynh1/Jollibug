package vn.fastfood.controller.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.model.Payment;
import vn.fastfood.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/order/{maDH}")
    public ResponseEntity<?> getPaymentByMaDH(
            @PathVariable("maDH") long maDH,
            @RequestParam(value = "maPT", required = false) String maPT) {
        Payment thanhToan = paymentService.getOrCreateDefaultPayment(maDH, maPT);
        if (thanhToan == null) {
            return error(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin thanh toán");
        }

        return ResponseEntity.ok(thanhToan);
    }

    @PutMapping("/order/{maDH}/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable("maDH") long maDH) {
        if (!paymentService.confirmPayment(maDH)) {
            return error(HttpStatus.BAD_REQUEST, "Không thể xác nhận thanh toán");
        }

        return ok("Xác nhận thanh toán thành công");
    }

    @PutMapping("/order/{maDH}/fail")
    public ResponseEntity<Map<String, Object>> failPayment(@PathVariable("maDH") long maDH) {
        if (!paymentService.failPayment(maDH)) {
            return error(HttpStatus.BAD_REQUEST, "Không thể cập nhật thanh toán thất bại");
        }

        return ok("Đã cập nhật thanh toán thất bại");
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
