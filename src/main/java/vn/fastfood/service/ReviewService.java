package vn.fastfood.service;

import vn.fastfood.dao.ReviewDAO;

public class ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public boolean addReview(long maDH, long maKH, long maMon, int sao, String noiDung) {
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

        return reviewDAO.insertReview(maDH, maKH, maMon, sao, noiDung.trim());
    }
}
