package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.CheckoutCartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutDAO {

    public List<CheckoutCartItem> getCheckoutItems(long customerId) {
        List<CheckoutCartItem> items = new ArrayList<>();

        String sql = """
            SELECT m.MaMon,
                   m.TenMon,
                   ct.SLuong,
                   m.Gia AS DonGia,
                   (ct.SLuong * m.Gia) AS ThanhTien
            FROM GIOHANG gh
            JOIN CHITIETGH ct ON gh.MaGH = ct.MaGH
            JOIN MONAN m ON ct.MaMon = m.MaMon
            WHERE gh.MaTK = ?
              AND m.IsAvailable = 1
            ORDER BY ct.added_at DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CheckoutCartItem item = new CheckoutCartItem();

                    item.setMaMon(rs.getLong("MaMon"));
                    item.setTenMon(rs.getString("TenMon"));
                    item.setSoLuong(rs.getInt("SLuong"));
                    item.setDonGia(rs.getBigDecimal("DonGia"));
                    item.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public Long findDiscountIdByCode(String discountCode) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return null;
        }

        String sql = """
            SELECT MaGG
            FROM MAGIAMGIA
            WHERE UPPER(MaCode) = UPPER(?)
              AND CURRENT_TIMESTAMP BETWEEN NgayBatDau AND NgayKetThuc
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, discountCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("MaGG");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigDecimal calculateDiscount(String discountCode, BigDecimal subtotal) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String sql = """
            SELECT LoaiGiam, MucGiam, DieuKien
            FROM MAGIAMGIA
            WHERE UPPER(MaCode) = UPPER(?)
              AND CURRENT_TIMESTAMP BETWEEN NgayBatDau AND NgayKetThuc
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, discountCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String loaiGiam = rs.getString("LoaiGiam");
                    BigDecimal mucGiam = rs.getBigDecimal("MucGiam");
                    BigDecimal dieuKien = rs.getBigDecimal("DieuKien");

                    if (dieuKien != null && subtotal.compareTo(dieuKien) < 0) {
                        return BigDecimal.ZERO;
                    }

                    if ("PERCENT".equalsIgnoreCase(loaiGiam)) {
                        return subtotal.multiply(mucGiam).divide(BigDecimal.valueOf(100));
                    }

                    if ("AMOUNT".equalsIgnoreCase(loaiGiam)) {
                        if (mucGiam.compareTo(subtotal) > 0) {
                            return subtotal;
                        }

                        return mucGiam;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public long createOrder(
            long customerId,
            Long maDC,
            BigDecimal tongTienMon,
            BigDecimal tienGiamGia,
            BigDecimal thanhTien,
            Long maGG,
            String ghiChu
    ) throws SQLException {

        String sql = """
            INSERT INTO DONHANG (
                MaTK_KH, MaTK_NV, NgayDat, MaDC,
                TongTienMon, TienGiamGia, ThanhTien,
                TrangThaiDon, MaGG, GhiChu
            )
            VALUES (?, NULL, CURRENT_TIMESTAMP, ?, ?, ?, ?, 'PENDING', ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"MaDH"})) {

            ps.setLong(1, customerId);

            if (maDC == null) {
                ps.setNull(2, Types.NUMERIC);
            } else {
                ps.setLong(2, maDC);
            }

            ps.setBigDecimal(3, tongTienMon);
            ps.setBigDecimal(4, tienGiamGia);
            ps.setBigDecimal(5, thanhTien);

            if (maGG == null) {
                ps.setNull(6, Types.NUMERIC);
            } else {
                ps.setLong(6, maGG);
            }

            ps.setString(7, ghiChu);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Không lấy được mã đơn hàng vừa tạo.");
    }

    public void createOrderItem(long orderId, CheckoutCartItem item) throws SQLException {
        String sql = """
            INSERT INTO CHITIETDH (
                MaDH, MaMon, TenMon, SoLuong, DonGia, ThanhTien
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, item.getMaMon());
            ps.setString(3, item.getTenMon());
            ps.setInt(4, item.getSoLuong());
            ps.setBigDecimal(5, item.getDonGia());
            ps.setBigDecimal(6, item.getThanhTien());

            ps.executeUpdate();
        }
    }

    public void createPayment(long orderId, String maPT, BigDecimal amount) throws SQLException {
        String sql = """
            INSERT INTO THANHTOAN (
                MaDH, MaPT, NgayTT, SoTien, TrangThaiTT
            )
            VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)
        """;

        String paymentStatus = "Pending";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setString(2, maPT);
            ps.setBigDecimal(3, amount);
            ps.setString(4, paymentStatus);

            ps.executeUpdate();
        }
    }

    public boolean isValidAddress(long customerId, Long maDC) {
        if (maDC == null) {
            return false;
        }

        String sql = """
            SELECT COUNT(*)
            FROM DIACHI
            WHERE MaDC = ?
              AND MaTK = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDC);
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

    public boolean isValidPaymentMethod(String maPT) {
        if (maPT == null || maPT.trim().isEmpty()) {
            return false;
        }

        String normalizedMaPT = maPT.trim().toUpperCase();

        if ("COD".equals(normalizedMaPT)
                || "BANK".equals(normalizedMaPT)
                || "EWALLET".equals(normalizedMaPT)
                || "CREDIT_CARD".equals(normalizedMaPT)) {
            return true;
        }

        String sql = """
            SELECT COUNT(*)
            FROM PHUONGTHUCTT
            WHERE MaPT = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, normalizedMaPT);

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
}
