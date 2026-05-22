package vn.fastfood.service;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.OrderStatusHistoryResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.OrderStatusHistory;
import java.util.ArrayList;
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
        if ("PENDING".equals(status)) {
            return updateCustomerStatus(orderId, customerId, status, "CANCELLED", null);
        }

        if ("CONFIRMED".equals(status)) {
            return updateCustomerStatus(orderId, customerId, status, "CANCEL_REQUESTED", null);
        }
        
        System.out.println("Đơn hàng ở trạng thái " + status + " nên không thể hủy.");
        return false;
    }
    
    public boolean confirmReceived(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);

        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = normalizeStatus(dh.getTrangThaiDon());

        if (!"SHIPPING".equals(status)) {
            System.out.println("Chỉ có thể xác nhận hàng khi đơn đang giao.");
            return false;
        }

        return updateCustomerStatus(orderId, customerId, status, "DELIVERED", null);
    }
    
    public boolean canReviewOrder(long orderId, long customerId) {
        Order dh = orderDAO.getOrderById(orderId, customerId);

        if(dh == null)
            return false;

        String status = normalizeStatus(dh.getTrangThaiDon());
        return "DELIVERED".equals(status);
    }

    public boolean reorder(long orderId, long customerId) {
        Order order = orderDAO.getOrderById(orderId, customerId);

        if (order == null) {
            System.out.println("[REORDER] Order not found or not owned by customerId=" + customerId);
            return false;
        }

        return orderDAO.addOrderItemsToCart(orderId, customerId) > 0;
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

    public List<OrderStatusHistoryResponse> getOrderStatusHistory(long orderId) {
        List<OrderStatusHistory> history = orderDAO.getOrderStatusHistory(orderId);
        List<OrderStatusHistoryResponse> responses = new ArrayList<>();

        for (OrderStatusHistory item : history) {
            responses.add(new OrderStatusHistoryResponse(
                    item.getTrangThaiCu(),
                    item.getTrangThaiMoi(),
                    item.getMaNguoiThucHien(),
                    item.getLyDo(),
                    item.getThoiGian(),
                    displayStatus(item.getTrangThaiMoi())
            ));
        }

        return responses;
    }

    public List<OrderStatusHistoryResponse> getOrderStatusHistoryForCustomer(long orderId, long customerId) {
        Order order = orderDAO.getOrderById(orderId, customerId);

        if (order == null) {
            return null;
        }

        return getOrderStatusHistory(orderId);
    }

    public boolean updateOrderStatusByStaff(long orderId, long staffId, String nextStatus, String cancelReason) {
        Order order = orderDAO.getOrderByIdForStaff(orderId);

        if (order == null) {
            System.out.println("[STAFF UPDATE] Không tìm thấy đơn hàng.");
            return false;
        }

        String currentStatus = normalizeStatus(order.getTrangThaiDon());
        String normalizedNextStatus = normalizeStatus(nextStatus);

        System.out.println("[STAFF UPDATE] orderId=" + orderId);
        System.out.println("[STAFF UPDATE] staffId=" + staffId);
        System.out.println("[STAFF UPDATE] currentStatus=" + currentStatus);
        System.out.println("[STAFF UPDATE] nextStatus=" + normalizedNextStatus);
        System.out.println("[STAFF UPDATE] cancelReason=" + cancelReason);

        if (!isValidStaffTransition(currentStatus, normalizedNextStatus)) {
            System.out.println("[STAFF UPDATE] BLOCKED: " + currentStatus + " -> " + normalizedNextStatus);
            return false;
        }

        boolean result;

        if ("CANCELLED".equals(normalizedNextStatus)) {
            result = orderDAO.updateOrderStatusStaffAndCancelReason(orderId, staffId, normalizedNextStatus, cancelReason);
        } else {
            result = orderDAO.updateOrderStatusAndStaff(orderId, staffId, normalizedNextStatus);
        }

        System.out.println("[STAFF UPDATE] result=" + result);
        return result;
    }

    private boolean updateCustomerStatus(
            long orderId,
            long customerId,
            String oldStatus,
            String newStatus,
            String reason
    ) {
        return orderDAO.updateOrderStatus(orderId, newStatus);
    }

    private String displayStatus(String status) {
        String normalizedStatus = normalizeStatus(status);

        switch (normalizedStatus) {
            case "PENDING":
                return "\u0110\u00e3 \u0111\u1eb7t h\u00e0ng";
            case "CONFIRMED":
                return "\u0110\u00e3 x\u00e1c nh\u1eadn";
            case "SHIPPING":
                return "\u0110ang giao";
            case "DELIVERED":
                return "\u0110\u00e3 giao";
            case "CANCEL_REQUESTED":
                return "Y\u00eau c\u1ea7u h\u1ee7y";
            case "CANCELLED":
                return "\u0110\u00e3 h\u1ee7y";
            default:
                return status;
        }
    }

    private boolean isValidStaffTransition(String current, String next) {
        if ("PENDING".equals(current) && "CONFIRMED".equals(next)) return true;
        if ("PENDING".equals(current) && "CANCELLED".equals(next)) return true;

        if ("CONFIRMED".equals(current) && "SHIPPING".equals(next)) return true;
        if ("CONFIRMED".equals(current) && "CANCELLED".equals(next)) return true;

        if ("SHIPPING".equals(current) && "DELIVERED".equals(next)) return true;

        if ("CANCEL_REQUESTED".equals(current) && "CANCELLED".equals(next)) return true;
        if ("CANCEL_REQUESTED".equals(current) && "CONFIRMED".equals(next)) return true;

        return false;
    }
}
