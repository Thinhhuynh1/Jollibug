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
            String deliveryName,
            String deliveryPhone,
            String deliveryEmail,
            String deliveryAddress,
            List<CheckoutCartItem> items
    ) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            //Chỉ đọc những dữ liệu đã commit - tránh dirty read
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                Long checkoutMaDC = maDC;
                if (checkoutMaDC == null || checkoutMaDC <= 0) {
                    checkoutMaDC = createCheckoutAddress(
                            conn,
                            maKH,
                            deliveryName,
                            deliveryPhone,
                            deliveryEmail,
                            deliveryAddress
                    );
                }

                long orderId = runCheckoutProcedure(
                        conn,
                        maKH,
                        checkoutMaDC,
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

            String serializedItems = serializeItems(items);
            System.out.println("CHECKOUT ITEMS = [" + serializedItems + "]");
            cs.setString(9, serializedItems);
            //cs.setString(9, serializeItems(items));
            cs.registerOutParameter(10, Types.NUMERIC);
            cs.execute();
            return cs.getLong(10);
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

    private Long createCheckoutAddress(
            Connection conn,
            long maKH,
            String deliveryName,
            String deliveryPhone,
            String deliveryEmail,
            String deliveryAddress
    ) throws SQLException {
        if (!hasText(deliveryName) || !hasText(deliveryPhone) || !hasText(deliveryAddress)) {
            return null;
        }

        try (CallableStatement cs = conn.prepareCall("{call PROC_CREATE_ADDRESS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setLong(1, maKH);
            cs.setString(2, "Địa chỉ đặt hàng");
            cs.setString(3, deliveryName.trim());
            cs.setString(4, deliveryPhone.trim());
            cs.setString(5, buildAddressLine(deliveryAddress, deliveryEmail));
            cs.setString(6, "-");
            cs.setString(7, "-");
            cs.setString(8, "-");
            cs.setInt(9, 0);
            cs.registerOutParameter(10, Types.NUMERIC);
            cs.executeUpdate();
            return cs.getLong(10);
        }
    }

    private String buildAddressLine(String deliveryAddress, String deliveryEmail) {
        String address = deliveryAddress == null ? "" : deliveryAddress.trim();
        if (!hasText(deliveryEmail)) {
            return address;
        }
        return address + " (Email: " + deliveryEmail.trim() + ")";
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
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
                .append(Math.round(item.getDonGia())).append('\t')
                .append(Math.round(item.getThanhTien()));
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
