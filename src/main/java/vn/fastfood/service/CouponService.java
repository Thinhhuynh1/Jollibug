package vn.fastfood.service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.repository.MaGiamGiaRepository;

@Service
public class CouponService {

    private final MaGiamGiaRepository couponRepository;

    public CouponService(MaGiamGiaRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Optional<MaGiamGia> findValidCoupon(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedCode = code.trim().toUpperCase();
        Optional<MaGiamGia> coupon = couponRepository.findByTenMa(normalizedCode);
        if (coupon.isEmpty()) {
            return Optional.empty();
        }

        MaGiamGia maGiamGia = coupon.get();
        if (!isCouponActive(maGiamGia)) {
            return Optional.empty();
        }

        return Optional.of(maGiamGia);
    }

    public List<MaGiamGia> listApplicableCoupons(double subtotal) {
        if (subtotal <= 0) {
            return List.of();
        }

        return couponRepository.findAll().stream()
                .filter(coupon -> isApplicable(coupon, subtotal))
                .sorted(Comparator.comparing(MaGiamGia::getTenMa))
                .collect(Collectors.toList());
    }

    public boolean isApplicable(MaGiamGia coupon, double subtotal) {
        return isCouponActive(coupon)
                && meetsMinimumOrder(coupon, subtotal)
                && calculateDiscount(coupon, subtotal) > 0;
    }

    public boolean meetsMinimumOrder(MaGiamGia coupon, double subtotal) {
        if (coupon == null || subtotal <= 0) {
            return false;
        }

        Double minimumOrder = coupon.getDieuKien();
        if (minimumOrder == null || minimumOrder <= 0) {
            return true;
        }

        return subtotal >= minimumOrder;
    }

    public String getMinimumOrderMessage(MaGiamGia coupon) {
        Double minimumOrder = coupon.getDieuKien();
        if (minimumOrder == null || minimumOrder <= 0) {
            return "Đơn hàng chưa đủ điều kiện để áp dụng mã này.";
        }

        NumberFormat format = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return "Mã này chỉ áp dụng cho đơn từ " + format.format(minimumOrder) + " VND.";
    }

    public double calculateDiscount(MaGiamGia coupon, double subtotal) {
        if (coupon == null || subtotal <= 0 || !meetsMinimumOrder(coupon, subtotal)) {
            return 0;
        }

        double discount = 0;
        String type = coupon.getLoaiGiam();
        Double amount = coupon.getMucGiam();

        if (amount == null || amount <= 0) {
            return 0;
        }

        if (type == null) {
            return 0;
        }

        switch (type.trim().toUpperCase()) {
            case "PERCENTAGE":
                discount = subtotal * (amount / 100.0);
                break;
            case "AMOUNT":
                discount = amount;
                break;
            default:
                discount = 0;
                break;
        }

        if (discount > subtotal) {
            discount = subtotal;
        }
        return discount;
    }

    private boolean isCouponActive(MaGiamGia coupon) {
        if (coupon == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getNgayBatDau() == null || coupon.getNgayKetThuc() == null) {
            return false;
        }

        if (now.isBefore(coupon.getNgayBatDau()) || now.isAfter(coupon.getNgayKetThuc())) {
            return false;
        }

        if (coupon.getSoLuong() != null && coupon.getSoLuong() <= 0) {
            return false;
        }

        return true;
    }
}
