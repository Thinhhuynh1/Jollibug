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
            SELECT m.MA_MON,
                   m.TEN_MON,
                   ct.SO_LUONG,
                   m.GIA AS DON_GIA,
                   (ct.SO_LUONG * m.GIA) AS THANH_TIEN
            FROM GIOHANG gh
            JOIN CHITIETGH ct ON gh.MAGH = ct.MAGH
            JOIN MONAN m ON ct.MA_MON = m.MA_MON
            WHERE gh.MATK = ?
              AND m.IS_AVAILABLE = 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CheckoutCartItem item = new CheckoutCartItem();

                    item.setMaMon(rs.getLong("MA_MON"));
                    item.setTenMon(rs.getString("TEN_MON"));
                    item.setSoLuong(rs.getInt("SO_LUONG"));
                    item.setDonGia(rs.getBigDecimal("DON_GIA"));
                    item.setThanhTien(rs.getBigDecimal("THANH_TIEN"));

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
            System.out.println("[CHECKOUT] Không tìm thấy bảng/cột mã giảm giá hoặc mã giảm giá không hợp lệ.");
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
            System.out.println("[CHECKOUT] Bỏ qua giảm giá do bảng/cột MAGIAMGIA chưa sẵn sàng.");
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
                MATK,
                MATK_KH,
                MATK_NV,
                NGAYDAT,
                NGAY_DAT,
                MADC,
                TONGTIEN,
                TONG_TIEN,
                TONGTIENMON,
                TIENGIAMGIA,
                THANHTIEN,
                TRANGTHAI,
                TRANG_THAI,
                TRANGTHAIDON,
                SDTNHANHANG,
                SDTNHAN_HANG,
                DIACHIGIAOHANG,
                DIA_CHI_GIAO_HANG,
                GHICHU,
                GHI_CHU,
                MAGG,
                UPDATED_AT
            )
            VALUES (
                ?,
                ?,
                NULL,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                'PENDING',
                'PENDING',
                'PENDING',
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"MADH"})) {

            String phone = extractBetween(ghiChu, "SĐT:", ";");
            String address = extractAfter(ghiChu, "Địa chỉ nhập:");

            ps.setLong(1, customerId);
            ps.setLong(2, customerId);

            if (maDC == null || maDC <= 0) {
                ps.setNull(3, Types.NUMERIC);
            } else {
                ps.setLong(3, maDC);
            }

            ps.setBigDecimal(4, thanhTien);
            ps.setBigDecimal(5, thanhTien);
            ps.setBigDecimal(6, tongTienMon);
            ps.setBigDecimal(7, tienGiamGia);
            ps.setBigDecimal(8, thanhTien);

            ps.setString(9, phone);
            ps.setString(10, phone);
            ps.setString(11, address);
            ps.setString(12, address);
            ps.setString(13, ghiChu);
            ps.setString(14, ghiChu);

            if (maGG == null) {
                ps.setNull(15, Types.NUMERIC);
            } else {
                ps.setLong(15, maGG);
            }

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
                MADH,
                MAMON,
                MA_MON,
                TENMON,
                SOLUONG,
                SO_LUONG,
                DONGIA,
                DON_GIA,
                THANHTIEN
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, item.getMaMon());
            ps.setLong(3, item.getMaMon());
            ps.setString(4, item.getTenMon());
            ps.setInt(5, item.getSoLuong());
            ps.setInt(6, item.getSoLuong());
            ps.setBigDecimal(7, item.getDonGia());
            ps.setBigDecimal(8, item.getDonGia());
            ps.setBigDecimal(9, item.getThanhTien());

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

        String paymentStatus = "COD".equalsIgnoreCase(maPT) ? "Pending" : "Paid";

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
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("[CHECKOUT] Không kiểm tra được địa chỉ, sẽ cho phép đặt bằng thông tin nhập tay.");
        }

        return false;
    }

    public boolean isValidPaymentMethod(String maPT) {
        if (maPT == null || maPT.trim().isEmpty()) {
            return false;
        }

        String sql = """
            SELECT COUNT(*)
            FROM PHUONGTHUCTT
            WHERE MAPT = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPT.trim().toUpperCase());

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

    private String extractBetween(String source, String startToken, String endToken) {
        if (source == null || startToken == null || endToken == null) {
            return "";
        }

        int startIndex = source.indexOf(startToken);

        if (startIndex < 0) {
            return "";
        }

        startIndex += startToken.length();

        int endIndex = source.indexOf(endToken, startIndex);

        if (endIndex < 0) {
            return source.substring(startIndex).trim();
        }

        return source.substring(startIndex, endIndex).trim();
    }

    private String extractAfter(String source, String token) {
        if (source == null || token == null) {
            return "";
        }

        int index = source.indexOf(token);

        if (index < 0) {
            return "";
        }

        return source.substring(index + token.length()).trim();
    }
}
