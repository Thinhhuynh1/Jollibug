package vn.fastfood.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.dao.ReviewDAO;
import vn.fastfood.dto.ReviewRequest;
import vn.fastfood.dto.ReviewResponse;
import vn.fastfood.entity.ChiTietDHId;
import vn.fastfood.entity.DanhGia;
import vn.fastfood.entity.DonHang;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.Review;
import vn.fastfood.repository.ChiTietDHRepository;
import vn.fastfood.repository.DanhGiaRepository;
import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.MonAnRepository;

@Service
public class ReviewService {
    public static final int REVIEW_EDIT_MONTHS = 2;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final DanhGiaRepository danhGiaRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDHRepository chiTietDHRepository;
    private final MonAnRepository monAnRepository;

    public ReviewService(
            DanhGiaRepository danhGiaRepository,
            DonHangRepository donHangRepository,
            ChiTietDHRepository chiTietDHRepository,
            MonAnRepository monAnRepository) {
        this.danhGiaRepository = danhGiaRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDHRepository = chiTietDHRepository;
        this.monAnRepository = monAnRepository;
    }

    public boolean addReview(long maDH, long maKH, long maMon, int sao, String noiDung, MultipartFile imageFile) {
        if (sao < 1 || sao > 5) {
            return false;
        }

        if (!reviewDAO.isOrderDelivered(maDH, maKH)
                || !reviewDAO.isFoodInOrder(maDH, maMon)
                || reviewDAO.hasReviewed(maDH, maKH, maMon)) {
            return false;
        }

        String imagePath;
        try {
            imagePath = storeReviewImage(imageFile);
        } catch (IllegalArgumentException | IOException e) {
            return false;
        }

        return reviewDAO.insertReview(maDH, maKH, maMon, sao, safeTrim(noiDung), imagePath);
    }

    public DanhGia addReview(long maDH, long maKH, long maMon, int sao, String noiDung) {
        validateStars(sao);
        DonHang donHang = findOwnedOrder(maDH, maKH);
        if (!"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            throw new IllegalArgumentException("Chi duoc danh gia don hang da giao.");
        }
        if (!chiTietDHRepository.existsById(new ChiTietDHId(maDH, maMon))) {
            throw new IllegalArgumentException("Mon an khong thuoc don hang nay.");
        }
        if (danhGiaRepository.existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(maDH, maMon, maKH)) {
            throw new IllegalArgumentException("Mon an nay da duoc danh gia trong don hang.");
        }

        MonAn monAn = monAnRepository.findById(maMon)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay mon an."));

        DanhGia danhGia = new DanhGia();
        danhGia.setDonHang(donHang);
        danhGia.setMonAn(monAn);
        danhGia.setMaTKKH(maKH);
        danhGia.setSao(sao);
        danhGia.setNoiDung(safeTrim(noiDung));
        return danhGiaRepository.save(danhGia);
    }

    public List<Review> getReviewsByOrder(long maDH, long maKH) {
        if (!reviewDAO.isOrderOwnedByCustomer(maDH, maKH)) {
            return null;
        }

        return reviewDAO.findReviewsByOrder(maDH, maKH);
    }

    public List<ReviewResponse> listByCustomer(long maKH) {
        return danhGiaRepository.findByMaTKKHOrderByNgayDGDesc(maKH)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReviewResponse> listByCustomer(long maKH, Long maDH) {
        if (maDH == null) {
            return listByCustomer(maKH);
        }
        return listByOrderAndCustomer(maDH, maKH);
    }

    public List<ReviewResponse> listByOrderAndCustomer(long maDH, long maKH) {
        findOwnedOrder(maDH, maKH);
        return danhGiaRepository.findByDonHang_MaDHAndMaTKKH(maDH, maKH)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DonHang> listDeliveredOrdersWithReviewableItems(long maKH) {
        return donHangRepository.findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(maKH, "DELIVERED")
                .stream()
                .filter(order -> !listReviewableItems(order.getMaDH(), maKH).isEmpty())
                .toList();
    }

    public List<vn.fastfood.entity.ChiTietDH> listReviewableItems(long maDH, long maKH) {
        DonHang donHang = findOwnedOrder(maDH, maKH);
        if (!"DELIVERED".equalsIgnoreCase(donHang.getTrangThai())) {
            return List.of();
        }

        return chiTietDHRepository.findByMaDH(maDH)
                .stream()
                .filter(item -> !danhGiaRepository.existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(
                        maDH, item.getMaMon(), maKH))
                .toList();
    }

    public int addReviewsBatch(long maDH, long maKH, List<ReviewRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap it nhat mot danh gia.");
        }

        int count = 0;
        for (ReviewRequest item : items) {
            if (item == null || item.getMaMon() <= 0) {
                continue;
            }
            addReview(maDH, maKH, item.getMaMon(), item.getSao(), item.getNoiDung());
            count++;
        }
        if (count == 0) {
            throw new IllegalArgumentException("Vui long nhap it nhat mot danh gia hop le.");
        }
        return count;
    }

    public Optional<DanhGia> findReview(long maDG, long maKH) {
        return danhGiaRepository.findByMaDGAndMaTKKH(maDG, maKH);
    }

    public DanhGia updateReview(long maDG, long maKH, int sao, String noiDung) {
        validateStars(sao);
        DanhGia danhGia = danhGiaRepository.findByMaDGAndMaTKKH(maDG, maKH)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay danh gia."));
        danhGia.setSao(sao);
        danhGia.setNoiDung(safeTrim(noiDung));
        return danhGiaRepository.save(danhGia);
    }

    public void deleteReview(long maDG, long maKH) {
        DanhGia danhGia = danhGiaRepository.findByMaDGAndMaTKKH(maDG, maKH)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay danh gia."));
        danhGiaRepository.delete(danhGia);
    }

    public ReviewResponse toResponse(DanhGia danhGia) {
        ReviewResponse response = ReviewResponse.from(danhGia);
        response.setCanEdit(canEditReview(danhGia));
        response.setEditDeadlineDisplay(getEditDeadline(danhGia).format(DISPLAY_FORMAT));
        return response;
    }

    public boolean canEditReview(DanhGia danhGia) {
        return danhGia != null
                && danhGia.getNgayDG() != null
                && !LocalDateTime.now().isAfter(getEditDeadline(danhGia));
    }

    public LocalDateTime getEditDeadline(DanhGia danhGia) {
        if (danhGia == null || danhGia.getNgayDG() == null) {
            return LocalDateTime.now();
        }
        return danhGia.getNgayDG().plusMonths(REVIEW_EDIT_MONTHS);
    }

    private DonHang findOwnedOrder(long maDH, long maKH) {
        DonHang donHang = donHangRepository.findById(maDH)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang."));
        if (donHang.getUser() == null || !Long.valueOf(maKH).equals(donHang.getUser().getMaTK())) {
            throw new IllegalArgumentException("Don hang khong thuoc khach hang nay.");
        }
        return donHang;
    }

    private void validateStars(int sao) {
        if (sao < 1 || sao > 5) {
            throw new IllegalArgumentException("So sao khong hop le.");
        }
    }

    private String storeReviewImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String originalFileName = imageFile.getOriginalFilename();
        String extension = getExtension(originalFileName);
        if (!ALLOWED_IMAGE_TYPES.contains(extension)) {
            throw new IllegalArgumentException("Dinh dang anh khong hop le.");
        }

        String fileName = "review-" + UUID.randomUUID() + "." + extension;
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "images",
                "reviews");
        Files.createDirectories(uploadDir);
        Files.copy(imageFile.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "reviews/" + fileName;
    }

    private String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
