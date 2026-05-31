package vn.fastfood.controller.client;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.entity.DanhGia;
import vn.fastfood.entity.User;
import vn.fastfood.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    public ReviewApiController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listReviews(HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        var data = reviewService.listByCustomer(user.getMaTK());
        return ResponseEntity.ok(Map.of("success", true, "total", data.size(), "data", data));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> getReview(@PathVariable long reviewId, HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        return reviewService.findReview(reviewId, user.getMaTK())
                .map(d -> ResponseEntity.ok(Map.of("success", true, "data", reviewService.toResponse(d))))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Khong tim thay danh gia.")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody ReviewRequest request,
            @RequestParam("orderId") long orderId,
            HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        try {
            DanhGia danhGia = reviewService.addReview(orderId, user.getMaTK(), request.getMaMon(),
                    request.getSao(), request.getNoiDung());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đánh giá thành công.",
                    "data", reviewService.toResponse(danhGia)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable long reviewId,
            @RequestBody ReviewRequest request,
            HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        try {
            DanhGia danhGia = reviewService.updateReview(reviewId, user.getMaTK(),
                    request.getSao(), request.getNoiDung());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật đánh giá thành công.",
                    "data", reviewService.toResponse(danhGia)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> listReviewsByOrder(@PathVariable long orderId,
            @RequestParam("customerId") long customerId,
            HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        if (user.getMaTK() != customerId) {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Không có quyền xem đánh giá."));
        }
        try {
            var data = reviewService.listByOrderAndCustomer(orderId, customerId);
            return ResponseEntity.ok(Map.of("success", true, "total", data.size(), "data", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable long reviewId, HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return unauthorized();
        }
        try {
            reviewService.deleteReview(reviewId, user.getMaTK());
            return ResponseEntity.ok(Map.of("success", true, "message", "Da xoa danh gia."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    private User requireUser(HttpSession session) {
        Object user = session.getAttribute("user");
        return user instanceof User ? (User) user : null;
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Vui long dang nhap."));
    }
}
