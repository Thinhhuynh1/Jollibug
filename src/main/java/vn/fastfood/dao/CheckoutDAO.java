package vn.fastfood.dao;

import java.sql.CallableStatement;
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
                    item.setDonGia(rs.getDouble("DONGIA"));
                    item.setThanhTien(rs.getDouble("THANHTIEN"));
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

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{? = call FUNC_FIND_DISCOUNT_ID(?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, discountCode.trim());
            cs.execute();

            long maGG = cs.getLong(1);
            return cs.wasNull() ? null : maGG;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tìm mã giảm giá.", e);
        }
    }

    public double calcDiscount(String discountCode, double subtotal) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return 0;
        }

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{? = call FUNC_CALC_DISCOUNT(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, discountCode.trim());
            cs.setDouble(3, subtotal);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tính tiền giảm giá.", e);
        }
    }

    public double calcSubtotal(double donGia, int soLuong) {
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{? = call FUNC_CALC_SUBTOTAL(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setDouble(2, donGia);
            cs.setInt(3, soLuong);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tính tạm tính đơn hàng.", e);
        }
    }

    public long createOrder(
            long customerId,
            Long maDC,
            double tongTienMon,
            double tienGiamGia,
            double thanhTien,
            Long maGG,
            String ghiChu
    ) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setLong(1, customerId);

            if (maDC == null || maDC <= 0) {
                cs.setNull(2, Types.NUMERIC);
            } else {
                cs.setLong(2, maDC);
            }

            cs.setDouble(3, tongTienMon);
            cs.setDouble(4, tienGiamGia);
            cs.setDouble(5, thanhTien);

            if (maGG == null) {
                cs.setNull(6, Types.NUMERIC);
            } else {
                cs.setLong(6, maGG);
            }

            if (ghiChu == null || ghiChu.trim().isEmpty()) {
                cs.setNull(7, Types.CLOB);
            } else {
                cs.setString(7, ghiChu.trim());
            }

            cs.registerOutParameter(8, Types.NUMERIC);
            cs.execute();
            return cs.getLong(8);
        }
    }

    public long createOrderWithItemsAndPayment(
            long customerId,
            Long maDC,
            double tongTienMon,
            double tienGiamGia,
            double thanhTien,
            Long maGG,
            String ghiChu,
            List<CheckoutCartItem> items,
            String maPT
    ) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                long orderId = createOrder(conn, customerId, maDC, tongTienMon, tienGiamGia, thanhTien, maGG, ghiChu);

                for (CheckoutCartItem item : items) {
                    createOrderItem(conn, orderId, item);
                }

                createPayment(conn, orderId, maPT, thanhTien);

                conn.commit();
                return orderId;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }

    private long createOrder(
            Connection conn,
            long customerId,
            Long maDC,
            double tongTienMon,
            double tienGiamGia,
            double thanhTien,
            Long maGG,
            String ghiChu
    ) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setLong(1, customerId);

            if (maDC == null || maDC <= 0) {
                cs.setNull(2, Types.NUMERIC);
            } else {
                cs.setLong(2, maDC);
            }

            cs.setDouble(3, tongTienMon);
            cs.setDouble(4, tienGiamGia);
            cs.setDouble(5, thanhTien);

            if (maGG == null) {
                cs.setNull(6, Types.NUMERIC);
            } else {
                cs.setLong(6, maGG);
            }

            if (ghiChu == null || ghiChu.trim().isEmpty()) {
                cs.setNull(7, Types.CLOB);
            } else {
                cs.setString(7, ghiChu.trim());
            }

            cs.registerOutParameter(8, Types.NUMERIC);
            cs.execute();
            return cs.getLong(8);
        }
    }

    public void createOrderItem(long orderId, CheckoutCartItem item) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER_ITEM(?, ?, ?, ?, ?, ?)}")) {

            cs.setLong(1, orderId);
            cs.setLong(2, item.getMaMon());
            cs.setString(3, item.getTenMon());
            cs.setInt(4, item.getSoLuong());
            cs.setDouble(5, item.getDonGia());
            cs.setDouble(6, item.getThanhTien());
            cs.execute();
        }
    }

    private void createOrderItem(Connection conn, long orderId, CheckoutCartItem item) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER_ITEM(?, ?, ?, ?, ?, ?)}")) {

            cs.setLong(1, orderId);
            cs.setLong(2, item.getMaMon());
            cs.setString(3, item.getTenMon());
            cs.setInt(4, item.getSoLuong());
            cs.setDouble(5, item.getDonGia());
            cs.setDouble(6, item.getThanhTien());
            cs.execute();
        }
    }

    public void createPayment(long orderId, String maPT, double amount) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{call PROC_CREATE_PAYMENT(?, ?, ?, ?)}")) {

            cs.setLong(1, orderId);
            cs.setString(2, maPT);
            cs.setDouble(3, amount);
            cs.setString(4, "PENDING");
            cs.execute();
        }
    }

    private void createPayment(Connection conn, long orderId, String maPT, double amount) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_PAYMENT(?, ?, ?, ?)}")) {

            cs.setLong(1, orderId);
            cs.setString(2, maPT);
            cs.setDouble(3, amount);
            cs.setString(4, "PENDING");
            cs.execute();
        }
    }

    public boolean isValidAddress(long customerId, Long maDC) {
        if (maDC == null || maDC <= 0) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{? = call FUNC_IS_VALID_ADDRESS(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, customerId);
            cs.setLong(3, maDC);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kiểm tra địa chỉ giao hàng. Oracle: " + e.getMessage(), e);
        }
    }

    public boolean isValidPTTT(String maPT) {
        if (maPT == null || maPT.trim().isEmpty()) {
            return false;
        }

        String normalizedMaPT = maPT.trim().toUpperCase();

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{? = call FUNC_IS_VALID_PTTT(?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, normalizedMaPT);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kiểm tra phương thức thanh toán. Oracle: " + e.getMessage(), e);
        }
    }
}
