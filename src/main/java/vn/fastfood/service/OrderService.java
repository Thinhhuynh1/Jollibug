package vn.fastfood.service;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    public List<Order> getOrdersByCustomerId(long customerId) {
        return orderDAO.getOrdersByCustomerId(customerId);
    }
    
    public Order getOrderById(long orderId, long customerId) {
        return orderDAO.getOrderById(orderId, customerId);
    }

    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        return orderDAO.getOrderItemsByOrderId(orderId);
    }

    public boolean requestCancelOrder(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);
        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = normalizeStatus(dh.getTrangThaiDon());
        if ("PENDING".equals(status))
            return orderDAO.updateOrderStatus(orderId, "CANCELLED");

        if ("CONFIRMED".equals(status))
            return orderDAO.updateOrderStatus(orderId, "CANCEL_REQUESTED");
        
        System.out.println("Đơn hàng ở trạng thái " + status + " nên không thể hủy.");
        return false;
    }
    public boolean confirmReceived(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);
        if (dh == null)
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");

        String status = normalizeStatus(dh.getTrangThaiDon());

        if (!"SHIPPING".equals(status)) {
            System.out.println("Chỉ có thể xác nhận hàng khi đơn đang giao.");
            return false;
        }

        return orderDAO.updateOrderStatus(orderId, "DELIVERED");
    }
    
    public boolean canReviewOrder(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);

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

    public List<Order> getOrdersForStaff(String status, String keyword, String fromDate, String toDate) {
        return orderDAO.getOrdersForStaff(status, keyword, fromDate, toDate);
    }

    public Order getOrderByIdForStaff(long orderId) {
        return orderDAO.getOrderByIdForStaff(orderId);
    }

    public boolean updateOrderStatusByStaff(long orderId, long staffId, String nextStatus) {
        Order order = orderDAO.getOrderByIdForStaff(orderId);

        if (order == null) {
            System.out.println("Không tìm thấy đơn hàng.");
            return false;
        }

        String currentStatus = normalizeStatus(order.getTrangThaiDon());
        String normalizedNextStatus = normalizeStatus(nextStatus);

        if (!isValidStaffTransition(currentStatus, normalizedNextStatus)) {
            System.out.println("Không thể chuyển trạng thái từ " + currentStatus + " sang " + normalizedNextStatus);
            return false;
        }

        return orderDAO.updateOrderStatusAndStaff(orderId, staffId, normalizedNextStatus);
    }

    private boolean isValidStaffTransition(String current, String next) {
        if ("PENDING".equals(current) && "CONFIRMED".equals(next)) return true;
        if ("CONFIRMED".equals(current) && "SHIPPING".equals(next)) return true;
        if ("SHIPPING".equals(current) && "DELIVERED".equals(next)) return true;

        if ("CANCEL_REQUESTED".equals(current) && "CANCELLED".equals(next)) return true;
        if ("CANCEL_REQUESTED".equals(current) && "CONFIRMED".equals(next)) return true;

        return false;
    }
}
