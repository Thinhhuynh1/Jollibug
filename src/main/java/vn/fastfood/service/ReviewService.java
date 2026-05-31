package vn.fastfood.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.entity.ChiTietDH;
import vn.fastfood.entity.DanhGia;
import vn.fastfood.entity.DonHang;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.Order;
import vn.fastfood.repository.ChiTietDHRepository;
import vn.fastfood.repository.DanhGiaRepository;
import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.MonAnRepository;

@Service
public class ReviewService {

    public static final int REVIEW_WINDOW_MONTHS = 6;
    public static final int REVIEW_EDIT_MONTHS = 2;
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> listByCustomer(long customerId, Long orderId) {
        if (orderId == null) {
            return listByCustomer(customerId);
        }
        return listByOrderAndCustomer(orderId, customerId);
    }

    public List<ReviewResponse> listByOrderAndCustomer(long orderId, long customerId) {
        findOwnedOrder(orderId, customerId);
        return danhGiaRepository.findByDonHang_MaDHAndMaTKKH(orderId, customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<DanhGia> findReview(long reviewId, long customerId) {
        return danhGiaRepository.findByMaDGAndMaTKKH(reviewId, customerId);
    }

    public DanhGia addReview(long orderId, long customerId, long maMon, int sao, String noiDung) {
        validateReviewInput(sao, noiDung);
        DonHang donHang = findDeliveredOrder(orderId, customerId);
        ensureWithinReviewWindow(donHang);
        ensureFoodInOrder(orderId, maMon);
        if (danhGiaRepository.existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(orderId, maMon, customerId)) {
            throw new IllegalArgumentException("Món này đã được đánh giá trong đơn hàng #" + orderId + ".");
        }

        MonAn monAn = monAnRepository.findProduct(maMon);
        if (monAn == null) {
            throw new IllegalArgumentException("Không tìm thấy món ăn.");
        }

        DanhGia danhGia = new DanhGia();
        danhGia.setMaTKKH(customerId);
        danhGia.setDonHang(donHang);
        danhGia.setMonAn(monAn);
        danhGia.setSao(sao);
        danhGia.setNoiDung(noiDung.trim());
        return danhGiaRepository.save(danhGia);
    }

    public int addReviewsBatch(long orderId, long customerId, List<ReviewRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đánh giá cho ít nhất một món.");
        }

        int success = 0;
        StringBuilder errors = new StringBuilder();

        for (ReviewRequest item : items) {
            if (item == null || item.getMaMon() <= 0) {
                continue;
            }
            try {
                addReview(orderId, customerId, item.getMaMon(), item.getSao(), item.getNoiDung());
                success++;
            } catch (IllegalArgumentException ex) {
                if (errors.length() > 0) {
                    errors.append(" ");
                }
                errors.append(ex.getMessage());
            }
        }

        if (success == 0) {
            throw new IllegalArgumentException(
                    errors.length() > 0 ? errors.toString() : "Không thể gửi đánh giá.");
        }

        return success;
    }

    public DanhGia updateReview(long reviewId, long customerId, int sao, String noiDung) {
        validateReviewInput(sao, noiDung);
        DanhGia danhGia = findReviewOrThrow(reviewId, customerId);
        ensureCanEditReview(danhGia);
        danhGia.setSao(sao);
        danhGia.setNoiDung(noiDung.trim());
        return danhGiaRepository.save(danhGia);
    }

    public void deleteReview(long reviewId, long customerId) {
        DanhGia danhGia = findReviewOrThrow(reviewId, customerId);
        danhGiaRepository.delete(danhGia);
    }

    public boolean canReviewOrder(DonHang donHang) {
        if (donHang == null || !"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            return false;
        }
        LocalDateTime deliveredAt = getDeliveredAt(donHang);
        if (deliveredAt == null) {
            return false;
        }
        return !LocalDateTime.now().isAfter(getReviewDeadline(deliveredAt));
    }

    public boolean canReviewOrder(long orderId, long customerId) {
        return donHangRepository.findById(orderId)
                .filter(order -> order.getUser() != null && order.getUser().getMaTK() == customerId)
                .map(this::canReviewOrder)
                .orElse(false);
    }

    public boolean canEditReview(DanhGia danhGia) {
        if (danhGia == null || danhGia.getNgayDG() == null) {
            return false;
        }
        return !LocalDateTime.now().isAfter(getEditDeadline(danhGia));
    }

    public LocalDateTime getDeliveredAt(DonHang donHang) {
        if (donHang == null || !"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            return null;
        }
        if (donHang.getUpdatedAt() != null) {
            return donHang.getUpdatedAt();
        }
        return donHang.getNgayDat();
    }

    public LocalDateTime getReviewDeadline(DonHang donHang) {
        LocalDateTime deliveredAt = getDeliveredAt(donHang);
        return deliveredAt == null ? LocalDateTime.MIN : getReviewDeadline(deliveredAt);
    }

    public LocalDateTime getReviewDeadline(LocalDateTime deliveredAt) {
        return deliveredAt.plusMonths(REVIEW_WINDOW_MONTHS);
    }

    public LocalDateTime getEditDeadline(DanhGia danhGia) {
        if (danhGia == null || danhGia.getNgayDG() == null) {
            return LocalDateTime.MIN;
        }
        return danhGia.getNgayDG().plusMonths(REVIEW_EDIT_MONTHS);
    }

    public ReviewResponse toResponse(DanhGia danhGia) {
        ReviewResponse response = ReviewResponse.from(danhGia);
        response.setCanEdit(canEditReview(danhGia));
        LocalDateTime deadline = getEditDeadline(danhGia);
        response.setEditDeadlineDisplay(deadline.equals(LocalDateTime.MIN) ? "" : deadline.format(DISPLAY));
        return response;
    }

    public void enrichOrderReviewMeta(Order order) {
        if (order == null) {
            return;
        }
        donHangRepository.findById(order.getMaDH()).ifPresent(donHang -> {
            boolean canReviewItems = hasReviewableItems(donHang, order.getMaTKKH());
            order.setCanReview(canReviewItems);
            LocalDateTime deadline = getReviewDeadline(donHang);
            order.setReviewDeadlineDisplay(
                    deadline.equals(LocalDateTime.MIN) ? "" : deadline.format(DISPLAY));
        });
    }

    public List<ChiTietDH> listReviewableItems(long orderId, long customerId) {
        DonHang donHang = findOwnedOrder(orderId, customerId);
        if (!canReviewOrder(donHang)) {
            return List.of();
        }
        return chiTietDHRepository.findByMaDH(orderId).stream()
                .filter(item -> !isItemReviewed(orderId, customerId, item.getMaMon()))
                .collect(Collectors.toList());
    }

    public List<DonHang> listDeliveredOrdersWithReviewableItems(long customerId) {
        return donHangRepository.findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(customerId, "DELIVERED").stream()
                .filter(this::canReviewOrder)
                .filter(order -> hasReviewableItems(order, customerId))
                .collect(Collectors.toList());
    }

    public boolean hasReviewableItems(DonHang donHang, long customerId) {
        if (!canReviewOrder(donHang)) {
            return false;
        }
        long orderId = donHang.getMaDH();
        return chiTietDHRepository.findByMaDH(orderId).stream()
                .anyMatch(item -> !isItemReviewed(orderId, customerId, item.getMaMon()));
    }

    public boolean canReviewItem(long orderId, long customerId, long maMon) {
        DonHang donHang = findOwnedOrder(orderId, customerId);
        if (!canReviewOrder(donHang)) {
            return false;
        }
        ensureFoodInOrder(orderId, maMon);
        return !isItemReviewed(orderId, customerId, maMon);
    }

    public List<DonHang> listDeliveredOrdersWithoutReview(long customerId, long maMon) {
        return donHangRepository.findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(customerId, "DELIVERED").stream()
                .filter(this::canReviewOrder)
                .filter(order -> chiTietDHRepository.findByMaDH(order.getMaDH()).stream()
                        .anyMatch(item -> item.getMaMon().equals(maMon)))
                .filter(order -> !danhGiaRepository
                        .existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(order.getMaDH(), maMon, customerId))
                .collect(Collectors.toList());
    }

    private DanhGia findReviewOrThrow(long reviewId, long customerId) {
        return findReview(reviewId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá."));
    }

    private DonHang findOwnedOrder(long orderId, long customerId) {
        DonHang donHang = donHangRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));
        if (donHang.getUser() == null || donHang.getUser().getMaTK() != customerId) {
            throw new IllegalArgumentException("Đơn hàng không thuộc tài khoản này.");
        }
        return donHang;
    }

    private DonHang findDeliveredOrder(long orderId, long customerId) {
        DonHang donHang = findOwnedOrder(orderId, customerId);
        if (!"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ được đánh giá đơn hàng đã giao.");
        }
        return donHang;
    }

    private void ensureWithinReviewWindow(DonHang donHang) {
        if (!canReviewOrder(donHang)) {
            throw new IllegalArgumentException(
                    "Chỉ có thể đánh giá trong vòng " + REVIEW_WINDOW_MONTHS + " tháng kể từ ngày giao hàng.");
        }
    }

    private void ensureFoodInOrder(long orderId, long maMon) {
        boolean exists = chiTietDHRepository.findByMaDH(orderId).stream()
                .anyMatch(item -> item.getMaMon().equals(maMon));
        if (!exists) {
            throw new IllegalArgumentException("Món ăn không thuộc đơn hàng này.");
        }
    }

    private boolean isItemReviewed(long orderId, long customerId, long maMon) {
        return danhGiaRepository.existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(orderId, maMon, customerId);
    }

    private void ensureCanEditReview(DanhGia danhGia) {
        if (!canEditReview(danhGia)) {
            throw new IllegalArgumentException(
                    "Chỉ có thể sửa đánh giá trong vòng " + REVIEW_EDIT_MONTHS + " tháng kể từ ngày đánh giá.");
        }
    }

    private void validateReviewInput(int sao, String noiDung) {
        if (sao < 1 || sao > 5) {
            throw new IllegalArgumentException("Số sao phải từ 1 đến 5.");
        }
        if (noiDung == null || noiDung.isBlank()) {
            throw new IllegalArgumentException("Nội dung đánh giá không được để trống.");
        }
    }
}
