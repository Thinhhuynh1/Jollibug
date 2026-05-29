package vn.fastfood.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.dao.ReviewDAO;
import vn.fastfood.model.Review;

public class ReviewService {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public boolean addReview(long maDH, long maKH, long maMon, int sao, String noiDung, MultipartFile imageFile) {
        if (sao < 1 || sao > 5) {
            System.out.println("Số sao không hợp lệ.");
            return false;
        }

        if (noiDung == null || noiDung.trim().isEmpty()) {
            System.out.println("Nội dung đánh giá không được để trống.");
            return false;
        }

        if (!reviewDAO.isOrderDelivered(maDH, maKH)) {
            System.out.println("Chỉ được đánh giá đơn hàng đã giao thành công.");
            return false;
        }

        if (!reviewDAO.isFoodInOrder(maDH, maMon)) {
            System.out.println("Món ăn không thuộc đơn hàng này.");
            return false;
        }

        if (reviewDAO.hasReviewed(maDH, maKH, maMon)) {
            System.out.println("Món ăn này đã được đánh giá trong đơn hàng.");
            return false;
        }

        String imagePath;
        try {
            imagePath = storeReviewImage(imageFile);
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Không thể lưu ảnh đánh giá: " + e.getMessage());
            return false;
        }

        return reviewDAO.insertReview(maDH, maKH, maMon, sao, noiDung.trim(), imagePath);
    }

    public List<Review> getReviewsByOrder(long maDH, long maKH) {
        if (!reviewDAO.isOrderOwnedByCustomer(maDH, maKH)) {
            return null;
        }

        return reviewDAO.findReviewsByOrder(maDH, maKH);
    }

    private String storeReviewImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String originalFileName = imageFile.getOriginalFilename();
        String extension = getExtension(originalFileName);
        if (!ALLOWED_IMAGE_TYPES.contains(extension)) {
            throw new IllegalArgumentException("Định dạng ảnh không hợp lệ.");
        }

        String fileName = "review-" + UUID.randomUUID() + "." + extension;
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "images", "reviews");
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
}
