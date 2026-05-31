package vn.fastfood.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.entity.DanhGia;
import vn.fastfood.entity.DonHang;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.ChiTietDHRepository;
import vn.fastfood.repository.DanhGiaRepository;
import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.MonAnRepository;

@Service
public class ReviewService {
    private final DanhGiaRepository danhGiaRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDHRepository chiTietDHRepository;
    private final MonAnRepository monAnRepository;

    public ReviewService(DanhGiaRepository danhGiaRepository,
            DonHangRepository donHangRepository,
            ChiTietDHRepository chiTietDHRepository,
            MonAnRepository monAnRepository) {
        this.danhGiaRepository = danhGiaRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDHRepository = chiTietDHRepository;
        this.monAnRepository = monAnRepository;
    }

    public List<ReviewResponse> listByCustomer(long customerId) {
        return danhGiaRepository.findByMaTKKHOrderByNgayDGDesc(customerId).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    public Optional<DanhGia> findReview(long reviewId, long customerId) {
        return danhGiaRepository.findByMaDGAndMaTKKH(reviewId, customerId);
    }

    public DanhGia addReview(long orderId, long customerId, long maMon, int sao, String noiDung) {
        validateReviewInput(sao, noiDung);
        DonHang donHang = findDeliveredOrder(orderId, customerId);
        ensureFoodInOrder(orderId, maMon);
        if (danhGiaRepository.existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(orderId, maMon, customerId)) {
            throw new IllegalArgumentException("Mon an nay da duoc danh gia trong don hang.");
        }

        MonAn monAn = monAnRepository.findProduct(maMon);
        if (monAn == null) {
            throw new IllegalArgumentException("Khong tim thay mon an.");
        }

        DanhGia danhGia = new DanhGia();
        danhGia.setMaTKKH(customerId);
        danhGia.setDonHang(donHang);
        danhGia.setMonAn(monAn);
        danhGia.setSao(sao);
        danhGia.setNoiDung(noiDung.trim());
        return danhGiaRepository.save(danhGia);
    }

    public DanhGia updateReview(long reviewId, long customerId, int sao, String noiDung) {
        validateReviewInput(sao, noiDung);
        DanhGia danhGia = findReviewOrThrow(reviewId, customerId);
        danhGia.setSao(sao);
        danhGia.setNoiDung(noiDung.trim());
        return danhGiaRepository.save(danhGia);
    }

    public void deleteReview(long reviewId, long customerId) {
        DanhGia danhGia = findReviewOrThrow(reviewId, customerId);
        danhGiaRepository.delete(danhGia);
    }

    public List<DonHang> listDeliveredOrdersWithoutReview(long customerId, long maMon) {
        return donHangRepository.findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(customerId, "DELIVERED").stream()
                .filter(order -> chiTietDHRepository.findByMaDH(order.getMaDH()).stream()
                        .anyMatch(item -> item.getMaMon().equals(maMon)))
                .filter(order -> !danhGiaRepository
                        .existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(order.getMaDH(), maMon, customerId))
                .collect(Collectors.toList());
    }

    private DanhGia findReviewOrThrow(long reviewId, long customerId) {
        return findReview(reviewId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay danh gia."));
    }

    private DonHang findDeliveredOrder(long orderId, long customerId) {
        DonHang donHang = donHangRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang."));
        if (donHang.getUser() == null || donHang.getUser().getMaTK() != customerId) {
            throw new IllegalArgumentException("Don hang khong thuoc tai khoan nay.");
        }
        if (!"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            throw new IllegalArgumentException("Chi duoc danh gia don hang da giao.");
        }
        return donHang;
    }

    private void ensureFoodInOrder(long orderId, long maMon) {
        boolean exists = chiTietDHRepository.findByMaDH(orderId).stream()
                .anyMatch(item -> item.getMaMon().equals(maMon));
        if (!exists) {
            throw new IllegalArgumentException("Mon an khong thuoc don hang nay.");
        }
    }

    private void validateReviewInput(int sao, String noiDung) {
        if (sao < 1 || sao > 5) {
            throw new IllegalArgumentException("So sao phai tu 1 den 5.");
        }
        if (noiDung == null || noiDung.isBlank()) {
            throw new IllegalArgumentException("Noi dung danh gia khong duoc de trong.");
        }
    }
}
