package vn.fastfood.dao;

// import bd, donhang
import vn.fastfood.model.DonHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAO {
    public List<DonHang> getOrdersByCustomerId(long customerId) {
        List<DonHang> orders = new ArrayList<>();

        String sql = """
            SELECT MaDH, MaTK_KH, MaTK_NV, NgayDat, MaDC,
                   TongTienMon, TienGiamGia, ThanhTien,
                   TrangThaiDon, MaGG, GhiChu
            FROM DONHANG
            WHERE MaTK_KH = ?
            ORDER BY NgayDat DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonHang order = new DonHang();
                    order.setMaDH(rs.getLong("MaDH"));
                    order.setMaTKKH(rs.getLong("MaTK_KH"));

                    long maTKNV = rs.getLong("MaTK_NV");
                    order.setMaTKNV(rs.wasNull() ? null : maTKNV);

                    order.setNgayDat(rs.getTimestamp("NgayDat"));

                    long maDC = rs.getLong("MaDC");
                    order.setMaDC(rs.wasNull() ? null : maDC);

                    order.setTongTienMon(rs.getBigDecimal("TongTienMon"));
                    order.setTienGiamGia(rs.getBigDecimal("TienGiamGia"));
                    order.setThanhTien(rs.getBigDecimal("ThanhTien"));
                    order.setTrangThaiDon(rs.getString("TrangThaiDon"));

                    long maGG = rs.getLong("MaGG");
                    order.setMaGG(rs.wasNull() ? null : maGG);

                    order.setGhiChu(rs.getString("GhiChu"));

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
