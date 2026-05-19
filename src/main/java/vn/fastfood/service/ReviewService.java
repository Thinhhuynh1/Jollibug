package vn.fastfood.service;

import vn.fastfood.dao.ReviewDAO;
import vn.fastfood.model.Review;

import java.util.List;

public class ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public boolean addReview(long orderId, long customerId, long maMon, int sao, String noiDung) {
        if (sao < 1 || sao > 5) {
            System.out.println("Số sao không hợp lệ.");
            return false;
        }

        if (noiDung == null || noiDung.trim().isEmpty()) {
            System.out.println("Nội dung đánh giá không được để trống.");
            return false;
        }

        if (!reviewDAO.isOrderDelivered(orderId, customerId)) {
            System.out.println("Chỉ được đánh giá đơn hàng đã giao thành công.");
            return false;
        }

        if (!reviewDAO.isFoodInOrder(orderId, maMon)) {
            System.out.println("Món ăn không thuộc đơn hàng này.");
            return false;
        }

        if (reviewDAO.hasReviewed(orderId, customerId, maMon)) {
            System.out.println("Món ăn này đã được đánh giá trong đơn hàng.");
            return false;
        }

        return reviewDAO.insertReview(orderId, customerId, maMon, sao, noiDung.trim());
    }

    public List<Review> getReviewsByCustomerId(long customerId) {
        return reviewDAO.getReviewsByCustomerId(customerId);
    }

    public List<Review> getReviewsForStaff(String rating, String keyword, String fromDate, String toDate) {
        return reviewDAO.getReviewsForStaff(rating, keyword, fromDate, toDate);
    }
}
