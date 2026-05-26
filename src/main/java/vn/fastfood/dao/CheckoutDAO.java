package vn.fastfood.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

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
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            //Chỉ đọc những dữ liệu đã commit - tránh dirty read
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                long orderId = runCheckoutProcedure(
                        conn,
                        maKH,
                        maDC,
                        tongTienMon,
                        tienGiamGia,
                        thanhTien,
                        maPT,
                        maGG,
                        ghiChu,
                        items
                );

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

    private long runCheckoutProcedure(
            Connection conn,
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
        try (CallableStatement cs = conn.prepareCall("{call PROC_CHECKOUT(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setLong(1, maKH);

            if (maDC == null || maDC <= 0) {
                cs.setNull(2, Types.NUMERIC);
            } else {
                cs.setLong(2, maDC);
            }

            cs.setDouble(3, tongTienMon);
            cs.setDouble(4, tienGiamGia);
            cs.setDouble(5, thanhTien);
            cs.setString(6, maPT == null ? null : maPT.trim().toUpperCase());

            if (maGG == null) {
                cs.setNull(7, Types.NUMERIC);
            } else {
                cs.setLong(7, maGG);
            }

            if (ghiChu == null || ghiChu.trim().isEmpty()) {
                cs.setNull(8, Types.CLOB);
            } else {
                cs.setString(8, ghiChu.trim());
            }

            cs.setString(9, serializeItems(items));
            cs.registerOutParameter(10, Types.NUMERIC);
            cs.execute();
            return cs.getLong(10);
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

    private String serializeItems(List<CheckoutCartItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Giỏ hàng trống");
        }

        StringBuilder payload = new StringBuilder();

        for (CheckoutCartItem item : items) {
            if (item == null) {
                continue;
            }

            if (payload.length() > 0) {
                payload.append('\n');
            }

            payload.append(item.getMaMon()).append('\t')
                    .append(sanitizeField(item.getTenMon())).append('\t')
                    .append(item.getSoLuong()).append('\t')
                    .append(item.getDonGia()).append('\t')
                    .append(item.getThanhTien());
        }

        return payload.toString();
    }

    private String sanitizeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
