package vn.fastfood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Review;

public class ReviewDAO {

    public List<Review> findReviewsByProduct(long maMon) {
        String sql = """
            SELECT dg.MaDG, dg.MaTK_KH, dg.MaMon, dg.MaDH, dg.Sao, dg.NoiDung, dg.NgayDG, nd.HoTen
            FROM DANHGIA dg
            JOIN NGUOIDUNG nd ON nd.MaTK = dg.MaTK_KH
            WHERE dg.MaMon = ?
            ORDER BY dg.NgayDG DESC
        """;

        List<Review> listReview = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = mapBasicReview(rs);
                    review.setTenKhachHang(rs.getString("HoTen"));
                    listReview.add(review);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listReview;
    }

    public List<Review> getReviewsByCustomerId(long customerId) {
        String sql = """
            SELECT dg.MaDG,
                   dg.MaTK_KH,
                   dg.MaMon,
                   dg.MaDH,
                   dg.Sao,
                   dg.NoiDung,
                   dg.NgayDG,
                   ma.TenMon,
                   ma.HinhAnh
            FROM DANHGIA dg
            JOIN MONAN ma ON ma.MaMon = dg.MaMon
            WHERE dg.MaTK_KH = ?
            ORDER BY dg.NgayDG DESC
        """;

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = mapBasicReview(rs);
                    review.setTenMon(rs.getString("TenMon"));
                    review.setImageUrl(rs.getString("HinhAnh"));
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> getReviewsForStaff(String rating, String keyword, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT dg.MaDG,
                   dg.MaTK_KH,
                   dg.MaMon,
                   dg.MaDH,
                   dg.Sao,
                   dg.NoiDung,
                   dg.NgayDG,
                   ma.TenMon,
                   ma.HinhAnh,
                   nd.HoTen,
                   nd.Email
            FROM DANHGIA dg
            JOIN MONAN ma ON ma.MaMon = dg.MaMon
            JOIN NGUOIDUNG nd ON nd.MaTK = dg.MaTK_KH
            WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (rating != null && !rating.isBlank()) {
            sql.append(" AND dg.Sao = ?");
            params.add(Integer.parseInt(rating.trim()));
        }

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(dg.NoiDung) LIKE ? OR LOWER(m.TenMon) LIKE ? OR LOWER(nd.HoTen) LIKE ?)");
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND dg.NgayDG >= TO_DATE(?, 'YYYY-MM-DD')");
            params.add(fromDate.trim());
        }

        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND dg.NgayDG < TO_DATE(?, 'YYYY-MM-DD') + 1");
            params.add(toDate.trim());
        }

        sql.append(" ORDER BY dg.NgayDG DESC");

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = mapBasicReview(rs);
                    review.setTenMon(rs.getString("TenMon"));
                    review.setImageUrl(rs.getString("HinhAnh"));
                    review.setTenKhachHang(rs.getString("HoTen"));
                    review.setEmailKhachHang(rs.getString("Email"));
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public double getAverageRatingByProduct(long maMon) {
        String sql = """
            SELECT NVL(AVG(Sao), 0)
            FROM DANHGIA
            WHERE MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countReviewsByProduct(long maMon) {
        String sql = """
            SELECT COUNT(*)
            FROM DANHGIA
            WHERE MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isOrderDelivered(long orderId, long customerId) {
        String sql = """
            SELECT COUNT(*)
            FROM DONHANG
            WHERE MaDH = ?
              AND MaTK_KH = ?
              AND UPPER(TrangThaiDon) = 'DELIVERED'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isFoodInOrder(long orderId, long maMon) {
        String sql = """
            SELECT COUNT(*)
            FROM CHITIETDH
            WHERE MaDH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasReviewed(long orderId, long customerId, long maMon) {
        String sql = """
            SELECT COUNT(*)
            FROM DANHGIA
            WHERE MaDH = ?
              AND MaTK_KH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, customerId);
            ps.setLong(3, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertReview(long orderId, long customerId, long maMon, int sao, String noiDung) {
        String sql = """
            INSERT INTO DANHGIA (MaTK_KH, MaMon, MaDH, Sao, NoiDung, NgayDG)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setLong(2, maMon);
            ps.setLong(3, orderId);
            ps.setInt(4, sao);
            ps.setString(5, noiDung);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Review mapBasicReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setMaDG(rs.getLong("MaDG"));
        review.setMaTKKH(rs.getLong("MaTK_KH"));
        review.setMaMon(rs.getLong("MaMon"));
        review.setMaDH(rs.getLong("MaDH"));
        review.setSao(rs.getInt("Sao"));
        review.setNoiDung(rs.getString("NoiDung"));
        review.setNgayDG(rs.getTimestamp("NgayDG"));
        return review;
    }
}
