package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDAO {

    public Payment getPaymentByOrderId(long orderId) {
        String sql = """
            SELECT MaTT, MaDH, MaPT, NgayTT, SoTien, TrangThaiTT
            FROM THANHTOAN
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();

                    payment.setMaTT(rs.getLong("MaTT"));
                    payment.setMaDH(rs.getLong("MaDH"));
                    payment.setMaPT(rs.getString("MaPT"));
                    payment.setNgayTT(rs.getTimestamp("NgayTT"));
                    payment.setSoTien(rs.getBigDecimal("SoTien"));
                    payment.setTrangThaiTT(rs.getString("TrangThaiTT"));

                    return payment;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updatePaymentStatus(long orderId, String status) {
        String sql = """
            UPDATE THANHTOAN
            SET TrangThaiTT = ?,
                NgayTT = CURRENT_TIMESTAMP
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}