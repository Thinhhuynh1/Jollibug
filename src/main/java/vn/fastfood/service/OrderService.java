package vn.fastfood.service;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import java.util.List;

public class OrderService {
    private final OrderDAO donHangDAO = new OrderDAO();

    public List<Order> getOrdersByCustomerId(long customerId) {
        return donHangDAO.getOrdersByCustomerId(customerId);
    }
    
    public Order getOrderById(long orderId, long customerId) {
        return donHangDAO.getOrderById(orderId, customerId);
    }

    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        return donHangDAO.getOrderItemsByOrderId(orderId);
    }

    public boolean requestCancelOrder(long orderId, long customerId) {
        Order dh = donHangDAO.getOrderById(orderId, customerId);
        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = normalizeStatus(dh.getTrangThaiDon());
        if ("PENDING".equals(status))
            return donHangDAO.updateOrderStatus(orderId, "CANCELLED");

        if ("CONFIRMED".equals(status))
            return donHangDAO.updateOrderStatus(orderId, "CANCEL_REQUESTED");
        
        System.out.println("Đơn hàng ở trạng thái " + status + " nên không thể hủy.");
        return false;
    }
    public boolean confirmReceived(long orderId, long customerId) {
        Order dh = donHangDAO.getOrderById(orderId, customerId);
        if (dh == null)
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");

        String status = normalizeStatus(dh.getTrangThaiDon());

        if (!"SHIPPING".equals(status)) {
            System.out.println("Chỉ có thể xác nhận hàng khi đơn đang giao.");
            return false;
        }

        return donHangDAO.updateOrderStatus(orderId, "DELIVERED");
    }
    
    public boolean canReviewOrder(long orderId, long customerId) {
        Order dh = donHangDAO.getOrderById(orderId, customerId);

        if(dh == null)
            return false;

        String status = normalizeStatus(dh.getTrangThaiDon());
        return "DELIVERED".equals(status);
    }

    private String normalizeStatus(String status) {
        if (status == null)
            return "";
        return status.trim().toUpperCase();
    }
}
