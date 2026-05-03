package vn.fastfood.test;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;

import java.util.List;

public class TestOrderDAO {
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();

        long customerId = 1; // đổi thành MaTK_KH có thật trong bảng DONHANG

        System.out.println("=== TEST LỊCH SỬ ĐƠN HÀNG ===");

        List<Order> orders = dao.getOrdersByCustomerId(customerId);

        if (orders.isEmpty()) {
            System.out.println("Không tìm thấy đơn hàng nào của khách hàng có mã: " + customerId);
            return;
        }

        for (Order donHang : orders) {
            System.out.println(donHang);

            System.out.println("--- Chi tiết đơn hàng ---");
            List<OrderItem> items = dao.getOrderItemsByOrderId(donHang.getMaDH());

            for (OrderItem item : items) {
                System.out.println(item);
            }

            System.out.println();
        }
    }
}