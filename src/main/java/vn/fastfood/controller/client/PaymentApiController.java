package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.model.Payment;
import vn.fastfood.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {
    private final PaymentService paymentService = new PaymentService();

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(
            @PathVariable("orderId") long orderId
    ) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);

        if (payment == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "success", false,
                            "message", "Không tìm thấy thông tin thanh toán."
                    )
            );
        }

        return ResponseEntity.ok(payment);
    }

    @PutMapping("/order/{orderId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @PathVariable("orderId") long orderId
    ) {
        boolean result = paymentService.confirmPayment(orderId);

        if (result) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Xác nhận thanh toán thành công."
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể xác nhận thanh toán."
                )
        );
    }

    @PutMapping("/order/{orderId}/fail")
    public ResponseEntity<Map<String, Object>> failPayment(
            @PathVariable("orderId") long orderId
    ) {
        boolean result = paymentService.failPayment(orderId);

        if (result) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Đã cập nhật thanh toán thất bại."
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể cập nhật thanh toán thất bại."
                )
        );
    }
}