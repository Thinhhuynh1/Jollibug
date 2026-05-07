package vn.fastfood.dao;

import vn.fastfood.config.DBConnection;
import vn.fastfood.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public Long getCartIdByCustomerId(long customerId) {
        String sql = """
            SELECT MaGH
            FROM GIOHANG
            WHERE MaTK = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("MaGH");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CartItem> getCartItemsByCustomerId(long customerId) {
        List<CartItem> items = new ArrayList<>();

        String sql = """
            SELECT gh.MaGH, ct.MaMon, ct.SLuong
            FROM GIOHANG gh
            JOIN CHITIETGH ct ON gh.MaGH = ct.MaGH
            WHERE gh.MaTK = ?
            ORDER BY ct.added_at DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setMaGH(rs.getLong("MaGH"));
                    item.setMaMon(rs.getLong("MaMon"));
                    item.setSoLuong(rs.getInt("SLuong"));
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean updateCartItemQuantity(long customerId, long maMon, int soLuong) {
        Long maGH = getCartIdByCustomerId(customerId);

        if (maGH == null) {
            return false;
        }

        String sql = """
            UPDATE CHITIETGH
            SET SLuong = ?
            WHERE MaGH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuong);
            ps.setLong(2, maGH);
            ps.setLong(3, maMon);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeCartItem(long customerId, long maMon) {
        Long maGH = getCartIdByCustomerId(customerId);

        if (maGH == null) {
            return false;
        }

        String sql = """
            DELETE FROM CHITIETGH
            WHERE MaGH = ?
              AND MaMon = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maGH);
            ps.setLong(2, maMon);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}