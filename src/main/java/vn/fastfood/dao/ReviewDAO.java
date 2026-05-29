package vn.fastfood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.fastfood.config.DBConnection;
import vn.fastfood.entity.User;
import vn.fastfood.model.Review;

public class ReviewDAO {
    private static volatile boolean reviewImageColumnChecked = false;

    private void ensureReviewImageColumn(Connection conn) throws SQLException {
        if (reviewImageColumnChecked) {
            return;
        }

        synchronized (ReviewDAO.class) {
            if (reviewImageColumnChecked) {
                return;
            }

            try (ResultSet rs = conn.getMetaData().getColumns(null, conn.getSchema(), "DANHGIA", "ANHDG")) {
                if (rs.next()) {
                    reviewImageColumnChecked = true;
                    return;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("ALTER TABLE DANHGIA ADD AnhDG VARCHAR2(255)")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                if (e.getErrorCode() != 1430) {
                    throw e;
                }
            }

            reviewImageColumnChecked = true;
        }
    }

    public List<Review> findReviewsByProduct(long maMon) {
        String sql = """
            SELECT dg.MaDG, dg.MaTK_KH, dg.MaMon, dg.MaDH, dg.Sao, dg.NoiDung, dg.AnhDG, dg.NgayDG, nd.HoTen
            FROM DANHGIA dg
            JOIN NGUOIDUNG nd ON nd.MaTK = dg.MaTK_KH
            WHERE dg.MaMon = ?
            ORDER BY dg.NgayDG DESC
        """;

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            ensureReviewImageColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, maMon);

                try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();

                    User khachHang = new User();
                    khachHang.setMaTK(rs.getLong("MaTK_KH"));
                    khachHang.setHoTen(rs.getString("HoTen"));

                    review.setMaDG(rs.getLong("MaDG"));
                    review.setMaTKKH(rs.getLong("MaTK_KH"));
                    review.setMaMon(rs.getLong("MaMon"));
                    review.setMaDH(rs.getLong("MaDH"));
                    review.setSao(rs.getInt("Sao"));
                    review.setNoiDung(rs.getString("NoiDung"));
                    review.setAnhDG(rs.getString("AnhDG"));
                    review.setNgayDG(rs.getTimestamp("NgayDG"));
                    review.setKhachHang(khachHang);
                    reviews.add(review);
                }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> findReviewsByOrder(long maDH, long maKH) {
        String sql = """
            SELECT
                dg.MaDG,
                dg.MaTK_KH,
                dg.MaMon,
                dg.MaDH,
                dg.Sao,
                dg.NoiDung,
                dg.AnhDG,
                dg.NgayDG,
                ctdh.TenMon,
                ma.image_url AS ImageUrl
            FROM DANHGIA dg
            LEFT JOIN CHITIETDH ctdh ON dg.MaDH = ctdh.MaDH AND dg.MaMon = ctdh.MaMon
            LEFT JOIN MONAN ma ON dg.MaMon = ma.MaMon
            WHERE dg.MaDH = ?
              AND dg.MaTK_KH = ?
            ORDER BY dg.NgayDG DESC, dg.MaDG DESC
        """;

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            ensureReviewImageColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, maDH);
                ps.setLong(2, maKH);

                try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setMaDG(rs.getLong("MaDG"));
                    review.setMaTKKH(rs.getLong("MaTK_KH"));
                    review.setMaMon(rs.getLong("MaMon"));
                    review.setMaDH(rs.getLong("MaDH"));
                    review.setSao(rs.getInt("Sao"));
                    review.setNoiDung(rs.getString("NoiDung"));
                    review.setAnhDG(rs.getString("AnhDG"));
                    review.setNgayDG(rs.getTimestamp("NgayDG"));
                    review.setTenMon(rs.getString("TenMon"));
                    review.setImg(rs.getString("ImageUrl"));
                    reviews.add(review);
                }
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

    public boolean isOrderDelivered(long maDH, long maKH) {
        String sql = """
            SELECT COUNT(*)
            FROM DONHANG
            WHERE MaDH = ?
              AND MaTK_KH = ?
              AND UPPER(TrangThaiDon) = 'DELIVERED'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            ps.setLong(2, maKH);

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

    public boolean isOrderOwnedByCustomer(long maDH, long maKH) {
        String sql = """
            SELECT COUNT(*)
            FROM DONHANG
            WHERE MaDH = ?
              AND MaTK_KH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            ps.setLong(2, maKH);

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

    public boolean isFoodInOrder(long maDH, long maMon) {
        String sql = """
            SELECT COUNT(*)
            FROM CHITIETDH
            WHERE MaDH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
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

    public boolean hasReviewed(long maDH, long maKH, long maMon) {
        String sql = """
            SELECT COUNT(*)
            FROM DANHGIA
            WHERE MaDH = ?
              AND MaTK_KH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            ps.setLong(2, maKH);
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

    public boolean insertReview(long maDH, long maKH, long maMon, int sao, String noiDung, String anhDG) {
        String sql = """
            INSERT INTO DANHGIA (MaTK_KH, MaMon, MaDH, Sao, NoiDung, AnhDG, NgayDG)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection()) {
            ensureReviewImageColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, maKH);
                ps.setLong(2, maMon);
                ps.setLong(3, maDH);
                ps.setInt(4, sao);
                ps.setString(5, noiDung);
                ps.setString(6, anhDG);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
