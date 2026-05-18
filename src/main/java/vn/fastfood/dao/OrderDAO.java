package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.OrderStatusHistory;
import vn.fastfood.model.ReorderCartItemCandidate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
            LEFT JOIN USERS u ON dh.MaTK_KH = u.MaTK
            LEFT JOIN DIACHI dc ON dh.MaDC = dc.MaDC
            LEFT JOIN THANHTOAN tt ON dh.MaDH = tt.MaDH
            LEFT JOIN PHUONGTHUCTT pt ON tt.MaPT = pt.MaPT
            WHERE dh.MaDH = ?
              AND dh.MaTK_KH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, customerId);

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

        enrichOrderItems(orderId, items);

        return items;
    }

    private void enrichOrderItems(long orderId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            for (OrderItem item : items) {
                enrichOrderItemImage(conn, item);
                enrichOrderItemReviewStatus(conn, orderId, item);
            }
        } catch (SQLException e) {
            System.out.println("[ORDER ITEMS] Could not enrich order items. Returning base order item data.");
            e.printStackTrace();
        }
    }

    private void enrichOrderItemImage(Connection conn, OrderItem item) {
        String sql = """
            SELECT image_url
            FROM MONAN
            WHERE MaMon = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getMaMon());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item.setImageUrl(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ORDER ITEMS] Could not load image for maMon=" + item.getMaMon());
        }
    }

    private void enrichOrderItemReviewStatus(Connection conn, long orderId, OrderItem item) {
        String sql = """
            SELECT COUNT(*)
            FROM DANHGIA dg
            JOIN DONHANG dh
              ON dg.MaDH = dh.MaDH
             AND dg.MaTK_KH = dh.MaTK_KH
            WHERE dg.MaDH = ?
              AND dg.MaMon = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, item.getMaMon());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item.setReviewed(rs.getInt(1) > 0);
                }
            }
        } catch (SQLException e) {
            item.setReviewed(false);
            System.out.println("[ORDER ITEMS] Could not load review status for maMon=" + item.getMaMon());
        }
    }

    public List<ReorderCartItemCandidate> getReorderCartItemCandidates(long orderId) {
        List<ReorderCartItemCandidate> items = new ArrayList<>();

        String orderItemsSql = """
            SELECT MaMon, TenMon, SoLuong
            FROM CHITIETDH
            WHERE MaDH = ?
            ORDER BY MaMon
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(orderItemsSql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReorderCartItemCandidate item = new ReorderCartItemCandidate();

                    item.setMaMon(rs.getLong("MaMon"));
                    item.setOrderTenMon(rs.getString("TenMon"));
                    item.setRequestedQuantity(rs.getInt("SoLuong"));

                    enrichReorderProduct(conn, item);

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void enrichReorderProduct(Connection conn, ReorderCartItemCandidate item) {
        String[] sqlCandidates = {
                """
                    SELECT TenMon, Gia, image_url, IsAvailable, SoLuongTon AS StockQuantity
                    FROM MONAN
                    WHERE MaMon = ?
                """,
                """
                    SELECT TenMon, Gia, image_url, IsAvailable, SLuongTon AS StockQuantity
                    FROM MONAN
                    WHERE MaMon = ?
                """,
                """
                    SELECT TenMon, Gia, IsAvailable
                    FROM MONAN
                    WHERE MaMon = ?
                """
        };

        for (String sql : sqlCandidates) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, item.getMaMon());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        item.setProductExists(false);
                        return;
                    }

                    item.setProductExists(true);
                    item.setCurrentTenMon(rs.getString("TenMon"));
                    item.setCurrentPrice(rs.getBigDecimal("Gia"));

                    try {
                        item.setImageUrl(rs.getString("image_url"));
                    } catch (SQLException ignored) {
                        item.setImageUrl(null);
                    }

                    int isAvailable = rs.getInt("IsAvailable");
                    item.setAvailable(!rs.wasNull() && isAvailable == 1);

                    try {
                        long stock = rs.getLong("StockQuantity");
                        item.setAvailableQuantity(rs.wasNull() ? Long.MAX_VALUE : stock);
                    } catch (SQLException ignored) {
                        item.setAvailableQuantity(Long.MAX_VALUE);
                    }

                    return;
                }
            } catch (SQLException ignored) {
                // Try the next schema variant.
            }
        }

        item.setProductExists(false);
    }

    public boolean insertOrderStatusHistory(
            long orderId,
            String oldStatus,
            String newStatus,
            String actorType,
            Long actorId,
            String reason
    ) {
        String sql = """
            INSERT INTO LICHSUTRANGTHAIDH (
                MaDH,
                TrangThaiCu,
                TrangThaiMoi,
                NguoiThucHienLoai,
                MaNguoiThucHien,
                LyDo,
                ThoiGian
            )
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            setNullableVarchar(ps, 2, oldStatus);
            ps.setString(3, newStatus);
            ps.setString(4, actorType);

            if (actorId == null) {
                ps.setNull(5, Types.NUMERIC);
            } else {
                ps.setLong(5, actorId);
            }

            setNullableClob(ps, 6, reason);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<OrderStatusHistory> getOrderStatusHistory(long orderId) {
        List<OrderStatusHistory> history = new ArrayList<>();

        String sql = """
            SELECT
                MaLS,
                MaDH,
                TrangThaiCu,
                TrangThaiMoi,
                NguoiThucHienLoai,
                MaNguoiThucHien,
                LyDo,
                ThoiGian
            FROM LICHSUTRANGTHAIDH
            WHERE MaDH = ?
            ORDER BY ThoiGian ASC, MaLS ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

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

    public int addOrderItemsToCart(long orderId, long customerId) {
        String orderItemsSql = """
            SELECT MaMon, SoLuong
            FROM CHITIETDH
            WHERE MaDH = ?
            ORDER BY MaMon
        """;

        String mergeCartItemSql = """
            MERGE INTO CHITIETGH ct
            USING (
                SELECT ? AS MaGH, ? AS MaMon, ? AS SLuong
                FROM dual
            ) src
            ON (ct.MaGH = src.MaGH AND ct.MaMon = src.MaMon)
            WHEN MATCHED THEN
                UPDATE SET ct.SLuong = ct.SLuong + src.SLuong
            WHEN NOT MATCHED THEN
                INSERT (MaGH, MaMon, SLuong, added_at)
                VALUES (src.MaGH, src.MaMon, src.SLuong, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                Long cartId = getOrCreateCartId(conn, customerId);
                int itemCount = 0;

                try (PreparedStatement itemPs = conn.prepareStatement(orderItemsSql);
                     PreparedStatement mergePs = conn.prepareStatement(mergeCartItemSql)) {

                    itemPs.setLong(1, orderId);

                    try (ResultSet rs = itemPs.executeQuery()) {
                        while (rs.next()) {
                            mergePs.setLong(1, cartId);
                            mergePs.setLong(2, rs.getLong("MaMon"));
                            mergePs.setInt(3, rs.getInt("SoLuong"));
                            mergePs.executeUpdate();
                            itemCount++;
                        }
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return itemCount;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
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

    public boolean updateCustomerOrderStatusIfCurrent(
            long orderId,
            long customerId,
            String expectedStatus,
            String newStatus
    ) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?
            WHERE MaDH = ?
              AND MaTK_KH = ?
              AND UPPER(TrangThaiDon) = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            ps.setLong(3, customerId);
            ps.setString(4, expectedStatus);

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

    public boolean updateOrderStatusAndStaffIfCurrent(
            long orderId,
            long staffId,
            String expectedStatus,
            String newStatus
    ) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = ?
            WHERE MaDH = ?
              AND UPPER(TrangThaiDon) = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setLong(3, orderId);
            ps.setString(4, expectedStatus);

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

    private void setNullableVarchar(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
            return;
        }

        ps.setString(index, value.trim());
    }

    private void setNullableClob(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.CLOB);
            return;
        }

        String trimmedValue = value.trim();
        ps.setCharacterStream(index, new StringReader(trimmedValue), trimmedValue.length());
    }

    private Long getOrCreateCartId(Connection conn, long customerId) throws SQLException {
        String findSql = """
            SELECT MaGH
            FROM GIOHANG
            WHERE MaTK = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("MaGH");
                }
            }
        }

        String insertSql = """
            INSERT INTO GIOHANG (MaTK, created_at)
            VALUES (?, CURRENT_TIMESTAMP)
        """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql, new String[]{"MaGH"})) {
            ps.setLong(1, customerId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("MaGH");
                }
            }
        }

        throw new SQLException("Could not create cart for customerId=" + customerId);
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

    private OrderStatusHistory mapResultSetToOrderStatusHistory(ResultSet rs) throws SQLException {
        OrderStatusHistory history = new OrderStatusHistory();

        history.setMaLS(rs.getLong("MaLS"));
        history.setMaDH(rs.getLong("MaDH"));
        history.setTrangThaiCu(rs.getString("TrangThaiCu"));
        history.setTrangThaiMoi(rs.getString("TrangThaiMoi"));
        history.setNguoiThucHienLoai(rs.getString("NguoiThucHienLoai"));

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
                MaTK_NV = ?
            WHERE MaDH = ?
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setLong(3, orderId);

            int rows = ps.executeUpdate();

            System.out.println("[DAO CANCEL] rows=" + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("[DAO CANCEL] SQL ERROR:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateOrderStatusStaffAndCancelReasonIfCurrent(
            long orderId,
            long staffId,
            String expectedStatus,
            String newStatus,
            String cancelReason
    ) {
        String sql = """
            UPDATE DONHANG
            SET TrangThaiDon = ?,
                MaTK_NV = ?
            WHERE MaDH = ?
              AND UPPER(TrangThaiDon) = ?
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, staffId);
            ps.setLong(3, orderId);
            ps.setString(4, expectedStatus);

            int rows = ps.executeUpdate();

            System.out.println("[DAO CANCEL] rows=" + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("[DAO CANCEL] SQL ERROR:");
            e.printStackTrace();
        }

        return false;
    }

}
