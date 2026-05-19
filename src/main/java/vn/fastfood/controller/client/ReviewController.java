package vn.fastfood.controller.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.entity.User;
import vn.fastfood.model.Review;
import vn.fastfood.service.ReviewService;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    @GetMapping("/my-reviews")
    public ResponseEntity<?> getMyReviews(HttpSession session) {
        Long sessionCustomerId = resolveSessionCustomerId(session);

        if (sessionCustomerId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập lại để xem đánh giá của bạn."
            ));
        }

        return ResponseEntity.ok(reviewService.getReviewsByCustomerId(sessionCustomerId));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getReviewsByCustomer(
            @RequestParam("customerId") long customerId,
            HttpSession session
    ) {
        Long sessionCustomerId = resolveSessionCustomerId(session);

        if (sessionCustomerId == null) {
            return ResponseEntity.ok(reviewService.getReviewsByCustomerId(customerId));
        }

        if (sessionCustomerId != customerId) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập lại để xem đánh giá của bạn."
            ));
        }

        return ResponseEntity.ok(reviewService.getReviewsByCustomerId(sessionCustomerId));
    }

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

    private Long resolveSessionCustomerId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            return user.getMaTK();
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Number number) {
            return number.longValue();
        }

        if (userIdObj instanceof String value) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
