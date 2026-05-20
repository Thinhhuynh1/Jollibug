package vn.fastfood.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.service.ReviewService;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<Map<String, Object>> addReview(
            @PathVariable("orderId") long orderId,
            @RequestBody ReviewRequest request
    ) {
        boolean result = reviewService.addReview(
                orderId,
                request.getCustomerId(),
                request.getMaMon(),
                request.getSao(),
                request.getNoiDung()
        );

        if (result) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Đánh giá thành công."
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", "Không thể gửi đánh giá. Chỉ được đánh giá đơn đã giao và món chưa từng được đánh giá."
                )
        );
    }
}