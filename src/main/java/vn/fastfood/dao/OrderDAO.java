package vn.fastfood.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.OrderStatusHistory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    public List<Order> getOrdersByMaKH(long maKH) {
        List<Order> orders = new ArrayList<>();

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

            ps.setLong(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Order getOrderByMaDH(long maDH, long maKH) {
        String sql = """
            SELECT
                dh.MaDH,
                dh.MaTK_KH,
                dh.MaTK_NV,
                dh.NgayDat,
                dh.MaDC,
                dh.TongTienMon,
                dh.TienGiamGia,
                dh.ThanhTien,
                dh.TrangThaiDon,
                dh.MaGG,
                dh.GhiChu,
                u.HoTen AS TenKhachHang,
                u.SDT AS SDTKhachHang,
                u.Email AS EmailKhachHang,
                dc.TenNguoiNhan AS TenNguoiNhan,
                dc.SDTNguoiNhan AS SDTNguoiNhan,
                TRIM(
                    NVL(dc.DiaChiCuThe, '') ||
                    CASE WHEN dc.PhuongXa IS NOT NULL AND dc.PhuongXa <> '-' THEN ', ' || dc.PhuongXa ELSE '' END ||
                    CASE WHEN dc.QuanHuyen IS NOT NULL AND dc.QuanHuyen <> '-' THEN ', ' || dc.QuanHuyen ELSE '' END ||
                    CASE WHEN dc.TinhThanh IS NOT NULL AND dc.TinhThanh <> '-' THEN ', ' || dc.TinhThanh ELSE '' END
                ) AS DiaChiGiaoHang,
                tt.MaPT AS MaPT,
                pt.TenPT AS TenPT,
                tt.TrangThaiTT AS TrangThaiTT
            FROM DONHANG dh
            LEFT JOIN NGUOIDUNG u ON dh.MaTK_KH = u.MaTK
            LEFT JOIN DIACHI dc ON dh.MaDC = dc.MaDC
            LEFT JOIN THANHTOAN tt ON dh.MaDH = tt.MaDH
            LEFT JOIN PHUONGTHUCTT pt ON tt.MaPT = pt.MaPT
            WHERE dh.MaDH = ?
              AND dh.MaTK_KH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            ps.setLong(2, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrderDetailResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Order> getOrdersForStaff(String status, String keyword, String fromDate, String toDate) {
        List<Order> orders = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT MaDH, MaTK_KH, MaTK_NV, NgayDat, MaDC,
                   TongTienMon, TienGiamGia, ThanhTien,
                   TrangThaiDon, MaGG, GhiChu
            FROM DONHANG
            WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND UPPER(TrangThaiDon) = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                AND (
                    TO_CHAR(MaDH) LIKE ?
                    OR TO_CHAR(MaTK_KH) LIKE ?
                    OR LOWER(GhiChu) LIKE ?
                )
            """);

            String search = "%" + keyword.trim().toLowerCase() + "%";
            params.add(search);
            params.add(search);
            params.add(search);
        }

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append(" AND TRUNC(NgayDat) >= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(fromDate.trim());
        }

        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append(" AND TRUNC(NgayDat) <= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(toDate.trim());
        }

        sql.append(" ORDER BY NgayDat DESC ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Order getOrderByIdForStaffWithConnection(Connection conn, long orderId) throws SQLException {
        String sql = """
            SELECT
                dh.MaDH,
                dh.MaTK_KH,
                dh.MaTK_NV,
                dh.NgayDat,
                dh.MaDC,
                dh.TongTienMon,
                dh.TienGiamGia,
                dh.ThanhTien,
                dh.TrangThaiDon,
                dh.MaGG,
                dh.GhiChu,

                u.HoTen AS TenKhachHang,
                u.SDT AS SDTKhachHang,
                u.Email AS EmailKhachHang,

                dc.TenNguoiNhan AS TenNguoiNhan,
                dc.SDTNguoiNhan AS SDTNguoiNhan,
                TRIM(
                    NVL(dc.DiaChiCuThe, '') ||
                    CASE WHEN dc.PhuongXa IS NOT NULL THEN ', ' || dc.PhuongXa ELSE '' END ||
                    CASE WHEN dc.QuanHuyen IS NOT NULL THEN ', ' || dc.QuanHuyen ELSE '' END ||
                    CASE WHEN dc.TinhThanh IS NOT NULL THEN ', ' || dc.TinhThanh ELSE '' END
                ) AS DiaChiGiaoHang,

                tt.MaPT AS MaPT,
                pt.TenPT AS TenPT,
                tt.TrangThaiTT AS TrangThaiTT

            FROM DONHANG dh
            LEFT JOIN NGUOIDUNG u ON dh.MaTK_KH = u.MaTK
            LEFT JOIN DIACHI dc ON dh.MaDC = dc.MaDC
            LEFT JOIN THANHTOAN tt ON dh.MaDH = tt.MaDH
            LEFT JOIN PHUONGTHUCTT pt ON tt.MaPT = pt.MaPT
            WHERE dh.MaDH = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStaffOrderDetailResultSetToOrder(rs);
                }
            }
        }
        return null;
    }

    public Map<String, Object> getOrderByIdForStaffWithDemo(long orderId, String mode, long delayMs) throws SQLException {
        int isolationLevel = "SAFE".equalsIgnoreCase(mode)
                ? Connection.TRANSACTION_SERIALIZABLE
                : Connection.TRANSACTION_READ_COMMITTED;
        String isolationLabel = isolationLevel == Connection.TRANSACTION_SERIALIZABLE
                ? "SERIALIZABLE"
                : "READ_COMMITTED";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();

            try {
                conn.setTransactionIsolation(isolationLevel);
                conn.setAutoCommit(false);

                // First read
                Order firstOrder = getOrderByIdForStaffWithConnection(conn, orderId);
                String firstStatus = firstOrder != null ? firstOrder.getTrangThaiDon() : "NOT_FOUND";

                // Sleep to allow another transaction to update
                sleep(delayMs);

                // Second read
                Order secondOrder = getOrderByIdForStaffWithConnection(conn, orderId);
                String secondStatus = secondOrder != null ? secondOrder.getTrangThaiDon() : "NOT_FOUND";

                conn.commit();

                return Map.of(
                        "success", true,
                        "order", secondOrder != null ? secondOrder : Map.of(),
                        "firstStatus", firstStatus != null ? firstStatus : "NOT_FOUND",
                        "secondStatus", secondStatus != null ? secondStatus : "NOT_FOUND",
                        "changed", !String.valueOf(firstStatus).equals(String.valueOf(secondStatus)),
                        "isolation", isolationLabel,
                        "mode", mode
                );

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
                conn.setTransactionIsolation(originalIsolation);
            }
        }
    }

    public Order getOrderByIdForStaffWithConnection(Connection conn, long orderId) throws SQLException {
        String sql = """
            SELECT
                dh.MaDH,
                dh.MaTK_KH,
                dh.MaTK_NV,
                dh.NgayDat,
                dh.MaDC,
                dh.TongTienMon,
                dh.TienGiamGia,
                dh.ThanhTien,
                dh.TrangThaiDon,
                dh.MaGG,
                dh.GhiChu,

                u.HoTen AS TenKhachHang,
                u.SDT AS SDTKhachHang,
                u.Email AS EmailKhachHang,

                dc.TenNguoiNhan AS TenNguoiNhan,
                dc.SDTNguoiNhan AS SDTNguoiNhan,
                TRIM(
                    NVL(dc.DiaChiCuThe, '') ||
                    CASE WHEN dc.PhuongXa IS NOT NULL THEN ', ' || dc.PhuongXa ELSE '' END ||
                    CASE WHEN dc.QuanHuyen IS NOT NULL THEN ', ' || dc.QuanHuyen ELSE '' END ||
                    CASE WHEN dc.TinhThanh IS NOT NULL THEN ', ' || dc.TinhThanh ELSE '' END
                ) AS DiaChiGiaoHang,

                tt.MaPT AS MaPT,
                pt.TenPT AS TenPT,
                tt.TrangThaiTT AS TrangThaiTT

            FROM DONHANG dh
            LEFT JOIN NGUOIDUNG u ON dh.MaTK_KH = u.MaTK
            LEFT JOIN DIACHI dc ON dh.MaDC = dc.MaDC
            LEFT JOIN THANHTOAN tt ON dh.MaDH = tt.MaDH
            LEFT JOIN PHUONGTHUCTT pt ON tt.MaPT = pt.MaPT
            WHERE dh.MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStaffOrderDetailResultSetToOrder(rs);
                }
            }
        }
        return null;
    }

    public Map<String, Object> getOrderByIdForStaffWithDemo(long orderId, String mode, long delayMs) throws SQLException {
        int isolationLevel = "SAFE".equalsIgnoreCase(mode)
                ? Connection.TRANSACTION_SERIALIZABLE
                : Connection.TRANSACTION_READ_COMMITTED;
        String isolationLabel = isolationLevel == Connection.TRANSACTION_SERIALIZABLE
                ? "SERIALIZABLE"
                : "READ_COMMITTED";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();

            try {
                conn.setTransactionIsolation(isolationLevel);
                conn.setAutoCommit(false);

                // First read
                Order firstOrder = getOrderByIdForStaffWithConnection(conn, orderId);
                String firstStatus = firstOrder != null ? firstOrder.getTrangThaiDon() : "NOT_FOUND";

                // Sleep to allow another transaction to update
                sleep(delayMs);

                // Second read
                Order secondOrder = getOrderByIdForStaffWithConnection(conn, orderId);
                String secondStatus = secondOrder != null ? secondOrder.getTrangThaiDon() : "NOT_FOUND";

                conn.commit();

                return Map.of(
                        "success", true,
                        "order", secondOrder != null ? secondOrder : Map.of(),
                        "firstStatus", firstStatus != null ? firstStatus : "NOT_FOUND",
                        "secondStatus", secondStatus != null ? secondStatus : "NOT_FOUND",
                        "changed", !String.valueOf(firstStatus).equals(String.valueOf(secondStatus)),
                        "isolation", isolationLabel,
                        "mode", mode
                );

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
                conn.setTransactionIsolation(originalIsolation);
            }
        }
    }

    public Order getOrderByIdForStaff(long orderId) {
        String sql = """
            SELECT
                dh.MaDH,
                dh.MaTK_KH,
                dh.MaTK_NV,
                dh.NgayDat,
                dh.MaDC,
                dh.TongTienMon,
                dh.TienGiamGia,
                dh.ThanhTien,
                dh.TrangThaiDon,
                dh.MaGG,
                dh.GhiChu,
                u.HoTen AS TenKhachHang,
                u.SDT AS SDTKhachHang,
                u.Email AS EmailKhachHang,
                dc.TenNguoiNhan AS TenNguoiNhan,
                dc.SDTNguoiNhan AS SDTNguoiNhan,
                TRIM(
                    NVL(dc.DiaChiCuThe, '') ||
                    CASE WHEN dc.PhuongXa IS NOT NULL THEN ', ' || dc.PhuongXa ELSE '' END ||
                    CASE WHEN dc.QuanHuyen IS NOT NULL THEN ', ' || dc.QuanHuyen ELSE '' END ||
                    CASE WHEN dc.TinhThanh IS NOT NULL THEN ', ' || dc.TinhThanh ELSE '' END
                ) AS DiaChiGiaoHang,
                tt.MaPT AS MaPT,
                pt.TenPT AS TenPT,
                tt.TrangThaiTT AS TrangThaiTT
            FROM DONHANG dh
            LEFT JOIN NGUOIDUNG u ON dh.MaTK_KH = u.MaTK
            LEFT JOIN DIACHI dc ON dh.MaDC = dc.MaDC
            LEFT JOIN THANHTOAN tt ON dh.MaDH = tt.MaDH
            LEFT JOIN PHUONGTHUCTT pt ON tt.MaPT = pt.MaPT
            WHERE dh.MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrderDetailResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<OrderItem> getOrderItemsByMaDH(long maDH) {
        List<OrderItem> items = new ArrayList<>();

        String sql = """
            SELECT
                ctdh.MaDH,
                ctdh.MaMon,
                ctdh.TenMon,
                ctdh.SoLuong,
                ctdh.DonGia,
                ctdh.ThanhTien,
                ma.image_url AS ImageUrl
            FROM CHITIETDH ctdh
            LEFT JOIN MONAN ma ON ctdh.MaMon = ma.MaMon
            WHERE ctdh.MaDH = ?
            ORDER BY ctdh.MaMon
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setMaDH(rs.getLong("MaDH"));
                    item.setMaMon(rs.getLong("MaMon"));
                    item.setTenMon(rs.getString("TenMon"));
                    item.setSoLuong(rs.getInt("SoLuong"));
                    item.setDonGia(rs.getBigDecimal("DonGia"));
                    item.setThanhTien(rs.getBigDecimal("ThanhTien"));
                    item.setImg(rs.getString("ImageUrl"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<OrderStatusHistory> getOrderStatusHistory(long maDH) {
        List<OrderStatusHistory> history = new ArrayList<>();

        String sql = """
            SELECT MaLS, MaDH, TrangThaiCu, TrangThaiMoi, MaNguoiThucHien, LyDo, ThoiGian
            FROM LICHSUTRANGTHAIDH
            WHERE MaDH = ?
            ORDER BY ThoiGian ASC, MaLS ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(mapResultSetToOrderStatusHistory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public boolean updateOrderStatus(long maDH, String newStatus) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, maDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ==================== PHANTOM READ DEMO ====================

    public Map<String, Object> countOrderStatsTwiceForPhantomReadDemo(
            String isolation, long delayMs) throws SQLException {
        int isolationLevel = "SERIALIZABLE".equalsIgnoreCase(isolation)
                ? Connection.TRANSACTION_SERIALIZABLE
                : Connection.TRANSACTION_READ_COMMITTED;
        String isolationLabel = isolationLevel == Connection.TRANSACTION_SERIALIZABLE
                ? "SERIALIZABLE" : "READ_COMMITTED";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation     = conn.getTransactionIsolation();

            try {
                conn.setTransactionIsolation(isolationLevel);
                conn.setAutoCommit(false);

                // Lần đọc 1 – đếm đơn hàng hôm nay
                Map<String, Object> firstRead  = countTodayOrderStatsSnapshot(conn);

                // Ngủ để giao dịch T2 kịp insert/thay đổi trạng thái
                sleep(delayMs);

                // Lần đọc 2 – cùng câu truy vấn range
                Map<String, Object> secondRead = countTodayOrderStatsSnapshot(conn);

                conn.commit();

                long firstTotal  = ((Number) firstRead.get("total")).longValue();
                long secondTotal = ((Number) secondRead.get("total")).longValue();

                Map<String, Object> result = new HashMap<>();
                result.put("success",   true);
                result.put("isolation", isolationLabel);
                result.put("delayMs",   delayMs);
                result.put("firstRead",  firstRead);
                result.put("secondRead", secondRead);
                result.put("changed",   firstTotal != secondTotal);
                result.put("diff",      secondTotal - firstTotal);
                return result;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
                conn.setTransactionIsolation(originalIsolation);
            }
        }
    }

    private Map<String, Object> countTodayOrderStatsSnapshot(Connection conn) throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS totalOrders,
                SUM(CASE WHEN TrangThaiDon = 'DELIVERED'  THEN 1 ELSE 0 END) AS delivered,
                SUM(CASE WHEN TrangThaiDon = 'PENDING'    THEN 1 ELSE 0 END) AS pending,
                SUM(CASE WHEN TrangThaiDon = 'CONFIRMED'  THEN 1 ELSE 0 END) AS confirmed,
                SUM(CASE WHEN TrangThaiDon = 'SHIPPING'   THEN 1 ELSE 0 END) AS shipping,
                SUM(CASE WHEN TrangThaiDon = 'CANCELLED'  THEN 1 ELSE 0 END) AS cancelled,
                COALESCE(SUM(ThanhTien), 0) AS tongDoanhThu
            FROM DONHANG
            WHERE NgayDat >= TRUNC(SYSDATE)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Map<String, Object> snap = new HashMap<>();
                snap.put("total",       rs.getLong("totalOrders"));
                snap.put("delivered",   rs.getLong("delivered"));
                snap.put("pending",     rs.getLong("pending"));
                snap.put("confirmed",   rs.getLong("confirmed"));
                snap.put("shipping",    rs.getLong("shipping"));
                snap.put("cancelled",   rs.getLong("cancelled"));
                snap.put("tongDoanhThu",rs.getLong("tongDoanhThu"));
                return snap;
            }
        }

        Map<String, Object> empty = new HashMap<>();
        empty.put("total", 0L); empty.put("delivered", 0L); empty.put("pending", 0L);
        empty.put("confirmed", 0L); empty.put("shipping", 0L); empty.put("cancelled", 0L);
        empty.put("tongDoanhThu", 0L);
        return empty;
    }

    private void sleep(long delayMs) {
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== PHANTOM READ DEMO ====================

    public Map<String, Object> countOrderStatsTwiceForPhantomReadDemo(
            String isolation, long delayMs) throws SQLException {
        int isolationLevel = "SERIALIZABLE".equalsIgnoreCase(isolation)
                ? Connection.TRANSACTION_SERIALIZABLE
                : Connection.TRANSACTION_READ_COMMITTED;
        String isolationLabel = isolationLevel == Connection.TRANSACTION_SERIALIZABLE
                ? "SERIALIZABLE" : "READ_COMMITTED";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation     = conn.getTransactionIsolation();

            try {
                conn.setTransactionIsolation(isolationLevel);
                conn.setAutoCommit(false);

                // Lần đọc 1 – đếm đơn hàng hôm nay
                Map<String, Object> firstRead  = countTodayOrderStatsSnapshot(conn);

                // Ngủ để giao dịch T2 kịp insert/thay đổi trạng thái
                sleep(delayMs);

                // Lần đọc 2 – cùng câu truy vấn range
                Map<String, Object> secondRead = countTodayOrderStatsSnapshot(conn);

                conn.commit();

                long firstTotal  = ((Number) firstRead.get("total")).longValue();
                long secondTotal = ((Number) secondRead.get("total")).longValue();

                Map<String, Object> result = new HashMap<>();
                result.put("success",   true);
                result.put("isolation", isolationLabel);
                result.put("delayMs",   delayMs);
                result.put("firstRead",  firstRead);
                result.put("secondRead", secondRead);
                result.put("changed",   firstTotal != secondTotal);
                result.put("diff",      secondTotal - firstTotal);
                return result;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
                conn.setTransactionIsolation(originalIsolation);
            }
        }
    }

    private Map<String, Object> countTodayOrderStatsSnapshot(Connection conn) throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS totalOrders,
                SUM(CASE WHEN TrangThaiDon = 'DELIVERED'  THEN 1 ELSE 0 END) AS delivered,
                SUM(CASE WHEN TrangThaiDon = 'PENDING'    THEN 1 ELSE 0 END) AS pending,
                SUM(CASE WHEN TrangThaiDon = 'CONFIRMED'  THEN 1 ELSE 0 END) AS confirmed,
                SUM(CASE WHEN TrangThaiDon = 'SHIPPING'   THEN 1 ELSE 0 END) AS shipping,
                SUM(CASE WHEN TrangThaiDon = 'CANCELLED'  THEN 1 ELSE 0 END) AS cancelled,
                COALESCE(SUM(ThanhTien), 0) AS tongDoanhThu
            FROM DONHANG
            WHERE NgayDat >= TRUNC(SYSDATE)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Map<String, Object> snap = new HashMap<>();
                snap.put("total",       rs.getLong("totalOrders"));
                snap.put("delivered",   rs.getLong("delivered"));
                snap.put("pending",     rs.getLong("pending"));
                snap.put("confirmed",   rs.getLong("confirmed"));
                snap.put("shipping",    rs.getLong("shipping"));
                snap.put("cancelled",   rs.getLong("cancelled"));
                snap.put("tongDoanhThu",rs.getLong("tongDoanhThu"));
                return snap;
            }
        }

        Map<String, Object> empty = new HashMap<>();
        empty.put("total", 0L); empty.put("delivered", 0L); empty.put("pending", 0L);
        empty.put("confirmed", 0L); empty.put("shipping", 0L); empty.put("cancelled", 0L);
        empty.put("tongDoanhThu", 0L);
        return empty;
    }

    private void sleep(long delayMs) {
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean updateOrderStatusAndStaff(long maDH, long staffId, String newStatus) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = CASE
                    WHEN MaTK_NV IS NULL THEN ?
                    ELSE MaTK_NV
                END
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setLong(3, maDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateOrderStatusStaffAndCancelReason(long maDH, long staffId, String newStatus, String cancelReason) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = CASE
                    WHEN MaTK_NV IS NULL THEN ?
                    ELSE MaTK_NV
                END,
                GhiChu = CASE
                    WHEN GhiChu IS NULL THEN TO_CLOB(?)
                    ELSE GhiChu || TO_CLOB(CHR(10) || ?)
                END
            WHERE MaDH = ?
        """;

        String reasonText = "[Há»§y Ä‘Æ¡n] LÃ½ do: "
                + (cancelReason == null || cancelReason.trim().isEmpty()
                ? "KhÃ´ng cÃ³ lÃ½ do cá»¥ thá»ƒ"
                : cancelReason.trim());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setString(3, reasonText);
            ps.setString(4, reasonText);
            ps.setLong(5, maDH);

            int rows = ps.executeUpdate();
            System.out.println("[DAO CANCEL] rows=" + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("[DAO CANCEL] SQL ERROR:");
            e.printStackTrace();
        }

        return false;
    }

    public void lockOrder(Connection conn, long maDH) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT MaDH
            FROM DONHANG
            WHERE MaDH = ?
            FOR UPDATE
        """)) {
            ps.setLong(1, maDH);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy đơn hàng");
                }
            }
        }
    }

    public void updateOrderStatus(Connection conn, long maDH, String newStatus) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            UPDATE DONHANG
            SET TrangThaiDon = ?
            WHERE MaDH = ?
        """)) {
            ps.setString(1, newStatus);
            ps.setLong(2, maDH);
            ps.executeUpdate();
        }
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
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
        return order;
    }

    private Order mapOrderDetailResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = mapResultSetToOrder(rs);
        order.setTenKhachHang(rs.getString("TenKhachHang"));
        order.setSdtKhachHang(rs.getString("SDTKhachHang"));
        order.setEmailKhachHang(rs.getString("EmailKhachHang"));
        order.setTenNguoiNhan(rs.getString("TenNguoiNhan"));
        order.setSdtNguoiNhan(rs.getString("SDTNguoiNhan"));
        order.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
        order.setMaPT(rs.getString("MaPT"));
        order.setTenPT(rs.getString("TenPT"));
        order.setTrangThaiTT(rs.getString("TrangThaiTT"));
        return order;
    }

    private OrderStatusHistory mapResultSetToOrderStatusHistory(ResultSet rs) throws SQLException {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setMaLS(rs.getLong("MaLS"));
        history.setMaDH(rs.getLong("MaDH"));
        history.setTrangThaiCu(rs.getString("TrangThaiCu"));
        history.setTrangThaiMoi(rs.getString("TrangThaiMoi"));

        long actorId = rs.getLong("MaNguoiThucHien");
        history.setMaNguoiThucHien(rs.wasNull() ? null : actorId);
        history.setLyDo(readClob(rs.getClob("LyDo")));
        history.setThoiGian(rs.getTimestamp("ThoiGian"));
        return history;
    }

    private String readClob(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();

        try (Reader reader = clob.getCharacterStream()) {
            char[] buffer = new char[4096];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                value.append(buffer, 0, charsRead);
            }

            return value.toString();
        } catch (IOException e) {
            throw new SQLException("Could not read order status history CLOB.", e);
        }
    }

    public boolean updateOrderStatusStaffAndCancelReason(long orderId, long staffId, String newStatus, String cancelReason) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = ?,
                GhiChu = CASE
                    WHEN GhiChu IS NULL OR DBMS_LOB.GETLENGTH(GhiChu) = 0 THEN TO_CLOB(?)
                    ELSE GhiChu || TO_CLOB(CHR(10) || ?)
                END
            WHERE MaDH = ?
        """;

        String reasonText = "[Hủy đơn] Lý do: "
                + (cancelReason == null || cancelReason.trim().isEmpty()
                ? "Không có lý do cụ thể"
                : cancelReason.trim());

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setString(3, reasonText);
            ps.setString(4, reasonText);
            ps.setLong(5, orderId);

            int rows = ps.executeUpdate();

            System.out.println("[DAO CANCEL] rows=" + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("[DAO CANCEL] SQL ERROR:");
            e.printStackTrace();
        }

        return false;
    }

    // ==================== PHANTOM READ DEMO ====================

    /**
     * T1: Doc COUNT(*) WHERE PENDING hai lan trong cung mot transaction, voi do tre o giua.
     * Trong khoang delay, T2 co the thay doi mot don -> PENDING va commit ngay.
     * READ_COMMITTED  -> T1 thay count thay doi (phantom xay ra).
     * SERIALIZABLE    -> T1 thay count nhat quan (phantom bi ngan chan).
     */
    public Map<String, Object> countPendingOrdersTwiceForPhantomReadDemo(
            String isolation, long delayMs) throws SQLException {

        int isolationLevel = "SERIALIZABLE".equalsIgnoreCase(isolation)
                ? Connection.TRANSACTION_SERIALIZABLE
                : Connection.TRANSACTION_READ_COMMITTED;
        String isolationLabel = isolationLevel == Connection.TRANSACTION_SERIALIZABLE
                ? "SERIALIZABLE" : "READ_COMMITTED";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();

            try {
                conn.setTransactionIsolation(isolationLevel);
                conn.setAutoCommit(false);

                long firstCount = countPendingOrdersForPhantomDemo(conn);
                sleep(delayMs);
                long secondCount = countPendingOrdersForPhantomDemo(conn);

                conn.commit();

                return Map.of(
                        "success",     true,
                        "isolation",   isolationLabel,
                        "delayMs",     delayMs,
                        "firstCount",  firstCount,
                        "secondCount", secondCount,
                        "phantom",     firstCount != secondCount
                );

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
                conn.setTransactionIsolation(originalIsolation);
            }
        }
    }

    /** Cau SQL dung trong moi lan doc cua demo Phantom Read. */
    private long countPendingOrdersForPhantomDemo(Connection conn) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS so_don
            FROM DONHANG
            WHERE TrangThaiDon = 'PENDING'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong("so_don") : 0L;
        }
    }

    /**
     * T2: Thay doi trang thai don hang (auto-commit, commit ngay lap tuc).
     * Goi tu Session 2 de tao ra 'phantom row' trong tap ket qua cua T1.
     */
    public boolean resetOrderStatusForPhantomReadDemo(long orderId, String status) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?
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

}
