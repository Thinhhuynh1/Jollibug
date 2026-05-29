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
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;

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
            SELECT MaDH, MaTK_KH, MaTK_NV, NgayDat, MaDC,
                   TongTienMon, TienGiamGia, ThanhTien,
                   TrangThaiDon, MaGG, GhiChu
            FROM DONHANG
            WHERE MaDH = ?
              AND MaTK_KH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            ps.setLong(2, maKH);

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

    public Order getOrderByMaDHForStaff(long maDH) {
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

            ps.setLong(1, maDH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStaffOrderDetailResultSetToOrder(rs);
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
            SELECT MaDH, MaMon, TenMon, SoLuong, DonGia, ThanhTien
            FROM CHITIETDH
            WHERE MaDH = ?
            ORDER BY MaMon
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
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean updateOrderStatus(long maDH, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call PROC_UPDATE_ORDER_STATUS(?, ?, ?, ?)}")) {
            cs.setLong(1, maDH);
            cs.setNull(2, Types.NUMERIC);
            cs.setString(3, newStatus);
            cs.setNull(4, Types.CLOB);
            cs.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateOrderStatusAndStaff(long maDH, long staffId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call PROC_UPDATE_ORDER_STATUS(?, ?, ?, ?)}")) {
            cs.setLong(1, maDH);
            cs.setLong(2, staffId);
            cs.setString(3, newStatus);
            cs.setNull(4, Types.CLOB);
            cs.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateOrderStatusAndStaff(Connection conn, long maDH, long staffId, String newStatus) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_UPDATE_ORDER_STATUS(?, ?, ?, ?)}")) {
            cs.setLong(1, maDH);
            cs.setLong(2, staffId);
            cs.setString(3, newStatus);
            cs.setNull(4, Types.CLOB);
            cs.execute();
        }
    }

    public boolean updateOrderStatusStaffAndCancelReason(long maDH, long staffId, String newStatus, String cancelReason) {
        String reasonText = "Hủy đơn - Lý do: ";
        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            reasonText += "Không có lý do cụ thể";
        }
        else {
            reasonText += cancelReason.trim();
        }

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call PROC_UPDATE_ORDER_STATUS(?, ?, ?, ?)}")) {
            cs.setLong(1, maDH);
            cs.setLong(2, staffId);
            cs.setString(3, newStatus);
            cs.setString(4, reasonText);
            cs.execute();
            return true;
        } catch (SQLException e) {
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
                    throw new SQLException("Khong tim thay don hang");
                }
            }
        }
    }

    public void updateOrderStatus(Connection conn, long maDH, String newStatus) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call PROC_UPDATE_ORDER_STATUS(?, ?, ?, ?)}")) {
            cs.setLong(1, maDH);
            cs.setNull(2, Types.NUMERIC);
            cs.setString(3, newStatus);
            cs.setNull(4, Types.CLOB);
            cs.execute();
        }
    }

    public boolean canChangeOrderStatus(String currentStatus, String nextStatus) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{? = call FUNC_CAN_CHANGE_ORDER_STATUS(?, ?)}")) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, currentStatus);
            cs.setString(3, nextStatus);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    private Order mapStaffOrderDetailResultSetToOrder(ResultSet rs) throws SQLException {
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
}
