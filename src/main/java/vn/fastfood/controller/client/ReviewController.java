package vn.fastfood.controller.client;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.service.ReviewService;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    @PostMapping("/{maDH}/reviews")
    public ResponseEntity<Map<String, Object>> addReview(
            @PathVariable("maDH") long maDH,
            @RequestBody ReviewRequest request) {
        boolean created = reviewService.addReview(
                maDH,
                request.getMaKH(),
                request.getMaMon(),
                request.getSao(),
                request.getNoiDung()
        );

        if (!created) {
            return response(
                    HttpStatus.BAD_REQUEST,
                    false,
                    "Không thể gửi đánh giá. Chỉ được đánh giá đơn đã giao và món chưa từng được đánh giá."
            );
        }

        return response(HttpStatus.OK, true, "Đánh giá thành công.");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, boolean success, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "success", success,
                "message", message
        ));
    }
}
