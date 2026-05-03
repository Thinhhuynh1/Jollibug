package vn.fastfood.test;

import vn.fastfood.dao.DonHangDAO;
import vn.fastfood.model.DonHang;
import vn.fastfood.model.ChiTietDH;

import java.util.List;

public class TestOrderDAO {
    public static void main(String[] args) {
        DonHangDAO dao = new DonHangDAO();

        long customerId = 1; // đổi thành MaTK_KH có thật trong bảng DONHANG

        System.out.println("=== TEST LỊCH SỬ ĐƠN HÀNG ===");

        List<DonHang> orders = dao.getOrdersByCustomerId(customerId);

        if (orders.isEmpty()) {
            System.out.println("Không tìm thấy đơn hàng nào của khách hàng có mã: " + customerId);
            return;
        }

        for (DonHang donHang : orders) {
            System.out.println(donHang);

            System.out.println("--- Chi tiết đơn hàng ---");
            List<ChiTietDH> items = dao.getOrderItemsByOrderId(donHang.getMaDH());

            for (ChiTietDH item : items) {
                System.out.println(item);
            }

            System.out.println();
        }
    }
}