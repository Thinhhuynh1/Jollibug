package vn.fastfood.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.model.Review;
import vn.fastfood.service.ReviewService;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    @GetMapping("/{maDH}/reviews")
    public ResponseEntity<?> getReviewsByOrder(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH) {
        List<Review> reviews = reviewService.getReviewsByOrder(maDH, maKH);
        if (reviews == null) {
            return response(HttpStatus.NOT_FOUND, false, "Không tìm thấy đơn hàng cho khách hàng này.");
        }

        return ResponseEntity.ok(reviews);
    }

    @PostMapping(value = "/{maDH}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addReview(
            @PathVariable("maDH") long maDH,
            @RequestParam("maKH") long maKH,
            @RequestParam("maMon") long maMon,
            @RequestParam("sao") int sao,
            @RequestParam("noiDung") String noiDung,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        boolean created = reviewService.addReview(
                maDH,
                maKH,
                maMon,
                sao,
                noiDung,
                image
        );

        if (!created) {
            return response(
                    HttpStatus.BAD_REQUEST,
                    false,
                    "Không thể gửi đánh giá. Chỉ được đánh giá đơn đã giao, món chưa từng được đánh giá và ảnh phải hợp lệ."
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
