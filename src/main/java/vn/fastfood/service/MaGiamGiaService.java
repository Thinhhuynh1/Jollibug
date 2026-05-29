package vn.fastfood.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.repository.MaGiamGiaRepository;

@Service
public class MaGiamGiaService {

    private final MaGiamGiaRepository couponRepository;

    public MaGiamGiaService(MaGiamGiaRepository couponRepository) {
        this.couponRepository = couponRepository;
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

        if (coupon.getSoLuong() != null && coupon.getSoLanSuDung() != null
                && coupon.getSoLanSuDung() >= coupon.getSoLuong()) {
            return false;
        }

        return true;
    }

    public Optional<MaGiamGia> findValidCoupon(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }

        String codedef = code.trim().toUpperCase();
        Optional<MaGiamGia> coupon = couponRepository.findByTenMa(codedef);
        if (coupon.isEmpty()) {
            return Optional.empty();
        }

        MaGiamGia maGiamGia = coupon.get();
        if (!isCouponActive(maGiamGia)) {
            return Optional.empty();
        }

        return Optional.of(maGiamGia);
    }

    public List<MaGiamGia> findActiveCoupons() {
        List<MaGiamGia> result = new ArrayList<>();

        for (MaGiamGia coupon : couponRepository.findAll()) {
            if (isCouponActive(coupon)) {
                result.add(coupon);
            }
        }

        result.sort(Comparator.comparing(MaGiamGia::getNgayKetThuc));
        return result;
    }

    public double calculateDiscount(MaGiamGia coupon, double subtotal) {
        if (coupon == null || subtotal <= 0) {
            return 0;
        }

        Double minimumOrderAmount = coupon.getDieuKien();
        if (minimumOrderAmount != null && minimumOrderAmount > 0 && subtotal < minimumOrderAmount) {
            return 0;
        }

        double discount = 0;
        String type = coupon.getLoaiGiam();
        Double amount = coupon.getMucGiam();

        if (amount == null || amount <= 0 || type == null) {
            return 0;
        }

        switch (type.trim().toUpperCase()) {
            case "PERCENT":
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

        int quantity = coupon.getSoLuong() == null ? 0 : coupon.getSoLuong();
        int used = coupon.getSoLanSuDung() == null ? 0 : coupon.getSoLanSuDung();
        if (quantity - used <= 0) {
            return false;
        }

        return true;
    }
}
