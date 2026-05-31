package vn.fastfood.controller.client;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.entity.DanhGia;
import vn.fastfood.service.ReviewService;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<Map<String, Object>> addReview(
            @PathVariable("orderId") long orderId,
            @RequestBody ReviewRequest request) {
        try {
            DanhGia danhGia = reviewService.addReview(
                    orderId,
                    request.getCustomerId(),
                    request.getMaMon(),
                    request.getSao(),
                    request.getNoiDung());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đánh giá thành công.",
                    "data", ReviewResponse.from(danhGia)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()));
        }
    }
}
