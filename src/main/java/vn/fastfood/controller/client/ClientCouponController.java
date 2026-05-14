package vn.fastfood.controller.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.service.CouponService;

@Controller
public class ClientCouponController {

    private final CouponService couponService;

    public ClientCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/api/voucher/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateVoucher(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "subtotal", defaultValue = "0") double subtotal) {

        Map<String, Object> payload = new HashMap<>();
        Optional<MaGiamGia> couponOpt = couponService.findValidCoupon(code);

        if (couponOpt.isEmpty()) {
            payload.put("valid", false);
            payload.put("message", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
            payload.put("discountAmount", 0);
            payload.put("total", subtotal);
            return ResponseEntity.ok(payload);
        }

        MaGiamGia coupon = couponOpt.get();
        double discountAmount = couponService.calculateDiscount(coupon, subtotal);
        double total = Math.max(0, subtotal - discountAmount);

        payload.put("valid", true);
        payload.put("message", "Áp dụng mã giảm giá thành công.");
        payload.put("code", coupon.getTenMa());
        payload.put("discountAmount", discountAmount);
        payload.put("total", total);
        payload.put("couponType", coupon.getLoaiGiam());
        payload.put("couponValue", coupon.getMucGiam());

        return ResponseEntity.ok(payload);
    }
}
