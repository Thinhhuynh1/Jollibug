package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}