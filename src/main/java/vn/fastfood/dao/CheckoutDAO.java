package vn.fastfood.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.CheckoutCartItem;

public class CheckoutDAO {

    public long checkout(
            long maKH,
            Long maDC,
            double tongTienMon,
            double tienGiamGia,
            double thanhTien,
            String maPT,
            Long maGG,
            String ghiChu,
            List<CheckoutCartItem> items
    ) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Giỏ hàng trống");
        }

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                lockProductsInOrder(conn, items);

                long orderId = createOrder(conn, maKH, maDC, tongTienMon, tienGiamGia, thanhTien, maGG, ghiChu);

                for (CheckoutCartItem item : items) {
                    createOrderItem(conn, orderId, item);
                }

                createPayment(conn, orderId, maPT, thanhTien);

                conn.commit();
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setTransactionIsolation(originalIsolation);
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public boolean isValidAddress(long maKH, Long maDC) {
        if (maDC == null || maDC <= 0) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{? = call FUNC_IS_VALID_ADDRESS(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, maKH);
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

    public double calcSubtotal(double donGia, int soLuong) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{? = call FUNC_CALC_SUBTOTAL(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setDouble(2, donGia);
            cs.setInt(3, soLuong);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi không thể tính tiền:  " + e.getMessage(), e);
        }
    }

    public double calcGiaGiam(String discountCode, double subtotal) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return 0;
        }

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{? = call FUNC_CALC_GIAGIAM(?, ?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, discountCode.trim());
            cs.setDouble(3, subtotal);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tính giảm giá: " + e.getMessage(), e);
        }
    }

    public int getSoLuongTon(long maMon) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{? = call FUNC_GET_SOLUONGTON(?)}")) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, maMon);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể lấy số lượng tồn: " + e.getMessage(), e);
        }
    }

    private void lockProductsInOrder(Connection conn, List<CheckoutCartItem> items) throws SQLException {
        Set<Long> productIds = new LinkedHashSet<>();

        items.stream()
                .filter(item -> item != null && item.getMaMon() > 0)
                .sorted(Comparator.comparingLong(CheckoutCartItem::getMaMon))
                .forEach(item -> productIds.add(item.getMaMon()));

        for (Long maMon : productIds) {
            lockProduct(conn, maMon);
        }
    }

    private void lockProduct(Connection conn, long maMon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT MaMon
            FROM MONAN
            WHERE MaMon = ?
            FOR UPDATE
        """)) {
            ps.setLong(1, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy món ăn " + maMon);
                }
            }
        }
    }

    private long createOrder(
            Connection conn,
            long maKH,
            Long maDC,
            double tongTienMon,
            double tienGiamGia,
            double thanhTien,
            Long maGG,
            String ghiChu
    ) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setLong(1, maKH);

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

    private void createOrderItem(Connection conn, long orderId, CheckoutCartItem item) throws SQLException {
        if (item == null) {
            return;
        }

        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ORDER_ITEM(?, ?, ?, ?, ?, ?)}")) {
            cs.setLong(1, orderId);
            cs.setLong(2, item.getMaMon());
            cs.setString(3, sanitizeField(item.getTenMon()));
            cs.setInt(4, item.getSoLuong());
            cs.setDouble(5, item.getDonGia());
            cs.setDouble(6, item.getThanhTien());
            cs.execute();
        }
    }

    private void createPayment(Connection conn, long orderId, String maPT, double soTien) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_PAYMENT(?, ?, ?, ?)}")) {
            cs.setLong(1, orderId);
            cs.setString(2, maPT == null ? null : maPT.trim().toUpperCase());
            cs.setDouble(3, soTien);
            cs.setString(4, "PENDING");
            cs.execute();
        }
    }

    private String sanitizeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
