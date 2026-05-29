package vn.fastfood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vn.fastfood.config.CouponUsageDemoSettings;
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

    public boolean confirmPaymentAndIncreaseCouponUsage(long orderId) {
        if (isLostUpdateDemoMode()) {
            return confirmPaymentAndIncreaseCouponUsageUnsafe(orderId);
        }

        return confirmPaymentAndIncreaseCouponUsageSafe(orderId);
    }

    private boolean confirmPaymentAndIncreaseCouponUsageSafe(long orderId) {
        String updatePaymentSql = """
            UPDATE THANHTOAN
            SET TrangThaiTT = 'PAID',
                NgayTT = CURRENT_TIMESTAMP
            WHERE MaDH = ?
              AND UPPER(TrangThaiTT) <> 'PAID'
        """;

        String lockCouponSql = """
            SELECT mg.MaGG
            FROM DONHANG dh
            JOIN MAGIAMGIA mg ON dh.MaGG = mg.MaGG
            WHERE dh.MaDH = ?
            FOR UPDATE OF mg.SoLanSuDung
        """;

        String increaseCouponUsageSql = """
            UPDATE MAGIAMGIA
            SET SoLanSuDung = NVL(SoLanSuDung, 0) + 1
            WHERE MaGG = ?
              AND NVL(SoLanSuDung, 0) < SoLuong
        """;

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement paymentPs = conn.prepareStatement(updatePaymentSql);
                 PreparedStatement lockCouponPs = conn.prepareStatement(lockCouponSql);
                 PreparedStatement couponPs = conn.prepareStatement(increaseCouponUsageSql)) {

                paymentPs.setLong(1, orderId);
                int updatedPayments = paymentPs.executeUpdate();

                if (updatedPayments == 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                Long couponId = findCouponId(lockCouponPs, orderId);
                if (couponId != null) {
                    waitForSafeCouponLockDemo();

                    couponPs.setLong(1, couponId);
                    int updatedCoupons = couponPs.executeUpdate();

                    if (updatedCoupons == 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return false;
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean confirmPaymentAndIncreaseCouponUsageUnsafe(long orderId) {
        String updatePaymentSql = """
            UPDATE THANHTOAN
            SET TrangThaiTT = 'PAID',
                NgayTT = CURRENT_TIMESTAMP
            WHERE MaDH = ?
              AND UPPER(TrangThaiTT) <> 'PAID'
        """;

        String readCouponUsageSql = """
            SELECT mg.MaGG, NVL(mg.SoLanSuDung, 0) AS SoLanSuDung
            FROM DONHANG dh
            JOIN MAGIAMGIA mg ON dh.MaGG = mg.MaGG
            WHERE dh.MaDH = ?
        """;

        String overwriteCouponUsageSql = """
            UPDATE MAGIAMGIA
            SET SoLanSuDung = ?
            WHERE MaGG = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement paymentPs = conn.prepareStatement(updatePaymentSql);
                 PreparedStatement readCouponPs = conn.prepareStatement(readCouponUsageSql);
                 PreparedStatement overwriteCouponPs = conn.prepareStatement(overwriteCouponUsageSql)) {

                paymentPs.setLong(1, orderId);
                int updatedPayments = paymentPs.executeUpdate();

                if (updatedPayments == 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                readCouponPs.setLong(1, orderId);
                try (ResultSet rs = readCouponPs.executeQuery()) {
                    if (rs.next()) {
                        long couponId = rs.getLong("MaGG");
                        int nextUsage = rs.getInt("SoLanSuDung") + 1;

                        waitForLostUpdateDemo();

                        overwriteCouponPs.setInt(1, nextUsage);
                        overwriteCouponPs.setLong(2, couponId);
                        overwriteCouponPs.executeUpdate();
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Long findCouponId(PreparedStatement ps, long orderId) throws SQLException {
        ps.setLong(1, orderId);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }

            long couponId = rs.getLong("MaGG");
            return rs.wasNull() ? null : couponId;
        }
    }

    private boolean isLostUpdateDemoMode() {
        return CouponUsageDemoSettings.isUnsafeMode();
    }

    private void waitForLostUpdateDemo() {
        long delayMs = CouponUsageDemoSettings.getUnsafeDelayMs();
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitForSafeCouponLockDemo() {
        long delayMs = CouponUsageDemoSettings.getSafeDelayMs();
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
