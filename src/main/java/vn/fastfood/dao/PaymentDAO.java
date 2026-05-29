package vn.fastfood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Payment;

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

    public boolean createPayment(long orderId, String maPT, double soTien, String trangThaiTT) {
        String sql = """
            INSERT INTO THANHTOAN (MaDH, MaPT, NgayTT, SoTien, TrangThaiTT)
            VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setString(2, maPT);
            ps.setDouble(3, soTien);
            ps.setString(4, trangThaiTT);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createPayment(Connection conn, long orderId, String maPT, double soTien, String trangThaiTT) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO THANHTOAN (MaDH, MaPT, NgayTT, SoTien, TrangThaiTT)
            VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)
        """)) {
            ps.setLong(1, orderId);
            ps.setString(2, maPT);
            ps.setDouble(3, soTien);
            ps.setString(4, trangThaiTT);
            return ps.executeUpdate() > 0;
        }
    }

    public void lockPayment(Connection conn, long orderId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT MaTT
            FROM THANHTOAN
            WHERE MaDH = ?
            FOR UPDATE
        """)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy thanh toán");
                }
            }
        }
    }

    public void updatePaymentStatus(Connection conn, long orderId, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            UPDATE THANHTOAN
            SET TrangThaiTT = ?,
                NgayTT = CURRENT_TIMESTAMP
            WHERE MaDH = ?
        """)) {
            ps.setString(1, status);
            ps.setLong(2, orderId);
            ps.executeUpdate();
        }
    }
}
