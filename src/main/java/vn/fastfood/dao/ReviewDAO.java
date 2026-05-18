package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Review;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewDAO {

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

    public List<Review> getReviewsByCustomerId(long customerId) {
        List<Review> reviews = new ArrayList<>();

        String sql = """
            SELECT
                MaDG,
                MaTK_KH,
                MaMon,
                MaDH,
                Sao,
                NoiDung,
                NgayDG
            FROM DANHGIA dg
            WHERE dg.MaTK_KH = ?
            ORDER BY dg.NgayDG DESC, dg.MaDG DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();

                    review.setMaDG(rs.getLong("MaDG"));
                    review.setMaTKKH(rs.getLong("MaTK_KH"));
                    review.setMaMon(rs.getLong("MaMon"));
                    review.setMaDH(rs.getLong("MaDH"));
                    review.setSao(rs.getInt("Sao"));
                    review.setNoiDung(readClob(rs.getClob("NoiDung")));
                    review.setNgayDG(rs.getTimestamp("NgayDG"));
                    enrichReviewFoodInfo(conn, review);

                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> getReviewsForStaff(String rating, String keyword, String fromDate, String toDate) {
        List<Review> reviews = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT
                dg.MaDG,
                dg.MaTK_KH,
                dg.MaMon,
                dg.MaDH,
                dg.Sao,
                dg.NoiDung,
                dg.NgayDG,
                u.HoTen AS TenKhachHang,
                u.Email AS EmailKhachHang
            FROM DANHGIA dg
            LEFT JOIN USERS u ON dg.MaTK_KH = u.MaTK
            WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (rating != null && !rating.trim().isEmpty()) {
            sql.append(" AND dg.Sao = ? ");
            params.add(Integer.parseInt(rating.trim()));
        }

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append(" AND TRUNC(dg.NgayDG) >= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(fromDate.trim());
        }

        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append(" AND TRUNC(dg.NgayDG) <= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(toDate.trim());
        }

        sql.append(" ORDER BY dg.NgayDG DESC, dg.MaDG DESC ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();

                    review.setMaDG(rs.getLong("MaDG"));
                    review.setMaTKKH(rs.getLong("MaTK_KH"));
                    review.setMaMon(rs.getLong("MaMon"));
                    review.setMaDH(rs.getLong("MaDH"));
                    review.setSao(rs.getInt("Sao"));
                    review.setNoiDung(readClob(rs.getClob("NoiDung")));
                    review.setNgayDG(rs.getTimestamp("NgayDG"));
                    review.setTenKhachHang(rs.getString("TenKhachHang"));
                    review.setEmailKhachHang(rs.getString("EmailKhachHang"));
                    enrichReviewFoodInfo(conn, review);

                    if (matchesStaffReviewKeyword(review, keyword)) {
                        reviews.add(review);
                    }
                }
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    private boolean matchesStaffReviewKeyword(Review review, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);

        return contains(review.getTenMon(), normalizedKeyword)
                || contains(review.getTenKhachHang(), normalizedKeyword)
                || contains(review.getEmailKhachHang(), normalizedKeyword)
                || contains(review.getNoiDung(), normalizedKeyword)
                || String.valueOf(review.getMaDH()).contains(normalizedKeyword)
                || String.valueOf(review.getMaTKKH()).contains(normalizedKeyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private void enrichReviewFoodInfo(Connection conn, Review review) {
        enrichReviewFoodFromOrderItem(conn, review);
        enrichReviewFoodFromProduct(conn, review);

        if (review.getTenMon() == null || review.getTenMon().trim().isEmpty()) {
            review.setTenMon("Món #" + review.getMaMon());
        }
    }

    private void enrichReviewFoodFromOrderItem(Connection conn, Review review) {
        String sql = """
            SELECT TenMon
            FROM CHITIETDH
            WHERE MaDH = ?
              AND MaMon = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, review.getMaDH());
            ps.setLong(2, review.getMaMon());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    review.setTenMon(rs.getString("TenMon"));
                }
            }
        } catch (SQLException ignored) {
            // Product name can still be loaded from MONAN.
        }
    }

    private void enrichReviewFoodFromProduct(Connection conn, Review review) {
        String[] sqlCandidates = {
                """
                    SELECT TenMon, image_url
                    FROM MONAN
                    WHERE MaMon = ?
                """,
                """
                    SELECT TenMon
                    FROM MONAN
                    WHERE MaMon = ?
                """
        };

        for (String sql : sqlCandidates) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, review.getMaMon());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }

                    String tenMon = rs.getString("TenMon");
                    if (tenMon != null && !tenMon.trim().isEmpty()) {
                        review.setTenMon(tenMon);
                    }

                    try {
                        review.setImageUrl(rs.getString("image_url"));
                    } catch (SQLException ignored) {
                        review.setImageUrl(null);
                    }

                    return;
                }
            } catch (SQLException ignored) {
                // Try the next schema variant.
            }
        }
    }

    private String readClob(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();

        try (Reader reader = clob.getCharacterStream()) {
            char[] buffer = new char[4096];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                value.append(buffer, 0, charsRead);
            }

            return value.toString();

        } catch (IOException e) {
            throw new SQLException("Could not read review content.", e);
        }
    }
}
