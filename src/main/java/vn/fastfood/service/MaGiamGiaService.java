package vn.fastfood.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.repository.MaGiamGiaRepository;

@Service
public class MaGiamGiaService {

    private final MaGiamGiaRepository couponRepository;
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();

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

        String codedef = code.trim();
        Optional<MaGiamGia> coupon = couponRepository.findByTenMaNormalized(codedef);
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
        if (coupon == null || subtotal <= 0 || coupon.getTenMa() == null || coupon.getTenMa().trim().isEmpty()) {
            return 0;
        }
        return checkoutDAO.calcGiaGiam(coupon.getTenMa(), subtotal);
    }
}
