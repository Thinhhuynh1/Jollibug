package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> getOrdersByCustomerId(long customerId) {
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

            ps.setLong(1, customerId);

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

    public Order getOrderById(long orderId, long customerId) {
        String sql = """
            SELECT MaDH, MaTK_KH, MaTK_NV, NgayDat, MaDC,
                   TongTienMon, TienGiamGia, ThanhTien,
                   TrangThaiDon, MaGG, GhiChu
            FROM DONHANG
            WHERE MaDH = ?
              AND MaTK_KH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
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

    public Order getOrderByIdForStaff(long orderId) {
        String sql = """
            SELECT MaDH, MaTK_KH, MaTK_NV, NgayDat, MaDC,
                   TongTienMon, TienGiamGia, ThanhTien,
                   TrangThaiDon, MaGG, GhiChu
            FROM DONHANG
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        List<OrderItem> items = new ArrayList<>();

        String sql = """
            SELECT MaDH, MaMon, TenMon, SoLuong, DonGia, ThanhTien
            FROM CHITIETDH
            WHERE MaDH = ?
            ORDER BY MaMon
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();

                    item.setMaDH(rs.getLong("MaDH"));
                    item.setMaMon(rs.getLong("MaMon"));
                    item.setTenMon(rs.getString("TenMon"));
                    item.setSoLuong(rs.getInt("SoLuong"));
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

    public boolean updateOrderStatus(long orderId, String newStatus) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateOrderStatusAndStaff(long orderId, long staffId, String newStatus) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = ?
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setLong(3, orderId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
}