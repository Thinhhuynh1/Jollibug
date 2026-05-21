package vn.fastfood.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.CheckoutCartItem;

public class CheckoutDAO {

    public List<CheckoutCartItem> getCheckoutItems(long customerId) {
        List<CheckoutCartItem> items = new ArrayList<>();

        String sql = """
            SELECT m.MAMON,
                   m.TENMON,
                   ct.SOLUONG,
                   m.GIA AS DONGIA,
                   (ct.SOLUONG * m.GIA) AS THANHTIEN
            FROM GIOHANG gh
            JOIN CHITIETGH ct ON gh.MAGH = ct.MAGH
            JOIN MONAN m ON ct.MAMON = m.MAMON
            WHERE gh.MATK = ?
              AND m.ISAVAILABLE = 1
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CheckoutCartItem item = new CheckoutCartItem();
                    item.setMaMon(rs.getLong("MAMON"));
                    item.setTenMon(rs.getString("TENMON"));
                    item.setSoLuong(rs.getInt("SOLUONG"));
                    item.setDonGia(rs.getBigDecimal("DONGIA"));
                    item.setThanhTien(rs.getBigDecimal("THANHTIEN"));
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
            SELECT MAGG
            FROM MAGIAMGIA
            WHERE UPPER(MACODE) = UPPER(?)
              AND CURRENT_TIMESTAMP BETWEEN NGAYBATDAU AND NGAYKETTHUC
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, discountCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("MAGG");
                }
            }

        } catch (SQLException e) {
            System.out.println("[CHECKOUT] Could not resolve discount id for code: " + discountCode);
        }

        return null;
    }

    public BigDecimal calculateDiscount(String discountCode, BigDecimal subtotal) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String sql = """
            SELECT LOAIGIAM, MUCGIAM, DIEUKIEN
            FROM MAGIAMGIA
            WHERE UPPER(MACODE) = UPPER(?)
              AND CURRENT_TIMESTAMP BETWEEN NGAYBATDAU AND NGAYKETTHUC
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, discountCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String loaiGiam = rs.getString("LOAIGIAM");
                    BigDecimal mucGiam = rs.getBigDecimal("MUCGIAM");
                    BigDecimal dieuKien = rs.getBigDecimal("DIEUKIEN");

                    if (dieuKien != null && subtotal.compareTo(dieuKien) < 0) {
                        return BigDecimal.ZERO;
                    }

                    if ("PERCENT".equalsIgnoreCase(loaiGiam) || "PERCENTAGE".equalsIgnoreCase(loaiGiam)) {
                        return subtotal.multiply(mucGiam).divide(BigDecimal.valueOf(100));
                    }

                    if ("AMOUNT".equalsIgnoreCase(loaiGiam)) {
                        return mucGiam.min(subtotal);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("[CHECKOUT] Skipping discount because MAGIAMGIA lookup failed.");
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
                MATK_KH,
                MATK_NV,
                NGAYDAT,
                MADC,
                TONGTIENMON,
                TIENGIAMGIA,
                THANHTIEN,
                TRANGTHAIDON,
                MAGG,
                GHICHU,
                UPDATED_AT
            )
            VALUES (?, NULL, CURRENT_TIMESTAMP, ?, ?, ?, ?, 'PENDING', ?, ?, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"MADH"})) {

            ps.setLong(1, customerId);

            if (maDC == null || maDC <= 0) {
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

            if (ghiChu == null || ghiChu.trim().isEmpty()) {
                ps.setNull(7, Types.CLOB);
            } else {
                ps.setString(7, ghiChu.trim());
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Could not get generated order id after creating DONHANG.");
    }

    public void createOrderItem(long orderId, CheckoutCartItem item) throws SQLException {
        String sql = """
            INSERT INTO CHITIETDH (
                MADH,
                MAMON,
                TENMON,
                SOLUONG,
                DONGIA,
                THANHTIEN
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
                MADH, MAPT, NGAYTT, SOTIEN, TRANGTHAITT
            )
            VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setString(2, maPT);
            ps.setBigDecimal(3, amount);
            ps.setString(4, "Pending");
            ps.executeUpdate();
        }
    }

    public boolean isValidAddress(long customerId, Long maDC) {
        if (maDC == null || maDC <= 0) {
            return false;
        }

        String sql = """
            SELECT COUNT(*)
            FROM DIACHI
            WHERE MADC = ?
              AND MATK = ?
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDC);
            ps.setLong(2, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("[CHECKOUT] Failed to validate address for customerId=" + customerId + ", maDC=" + maDC);
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
            WHERE MAPT = ?
        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, normalizedMaPT);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
