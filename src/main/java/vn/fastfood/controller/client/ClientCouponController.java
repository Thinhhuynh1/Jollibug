package vn.fastfood.controller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/api/voucher/available")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listAvailableVouchers(
            @RequestParam(name = "subtotal", defaultValue = "0") double subtotal) {

        List<Map<String, Object>> vouchers = new ArrayList<>();
        for (MaGiamGia coupon : couponService.listApplicableCoupons(subtotal)) {
            vouchers.add(toVoucherPayload(coupon, subtotal));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("vouchers", vouchers);
        payload.put("count", vouchers.size());
        return ResponseEntity.ok(payload);
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
        if (!couponService.meetsMinimumOrder(coupon, subtotal)) {
            payload.put("valid", false);
            payload.put("message", couponService.getMinimumOrderMessage(coupon));
            payload.put("discountAmount", 0);
            payload.put("total", subtotal);
            return ResponseEntity.ok(payload);
        }

        double discountAmount = couponService.calculateDiscount(coupon, subtotal);
        double total = Math.max(0, subtotal - discountAmount);

        payload.put("valid", true);
        payload.put("message", "Áp dụng mã giảm giá thành công.");
        payload.put("code", coupon.getTenMa());
        payload.put("discountAmount", discountAmount);
        payload.put("total", total);
        payload.put("couponType", coupon.getLoaiGiam());
        payload.put("couponValue", coupon.getMucGiam());
        payload.put("description", coupon.getMoTa());
        payload.put("discountDisplay", coupon.getDiscountDisplay());

        return ResponseEntity.ok(payload);
    }

    private Map<String, Object> toVoucherPayload(MaGiamGia coupon, double subtotal) {
        Map<String, Object> item = new HashMap<>();
        item.put("code", coupon.getTenMa());
        item.put("description", coupon.getMoTa());
        item.put("discountDisplay", coupon.getDiscountDisplay());
        item.put("couponType", coupon.getLoaiGiam());
        item.put("couponValue", coupon.getMucGiam());
        item.put("minimumOrder", coupon.getDieuKien());
        item.put("minimumOrderDisplay", coupon.getMinimumOrderDisplay());
        item.put("estimatedDiscount", couponService.calculateDiscount(coupon, subtotal));
        return item;
    }
}
