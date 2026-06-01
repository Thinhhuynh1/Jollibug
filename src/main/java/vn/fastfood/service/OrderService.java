package vn.fastfood.service;

import java.util.ArrayList;
import java.util.List;

import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.OrderStatusHistoryResponse;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;
import vn.fastfood.model.OrderStatusHistory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    private String displayStatus(String status) {
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();

        switch (normalizedStatus) {
            case "PENDING":
                return "Đã đặt hàng";
            case "CONFIRMED":
                return "Đã xác nhận";
            case "SHIPPING":
                return "Đang giao";
            case "DELIVERED":
                return "Đã giao";
            case "CANCEL_REQUESTED":
                return "Yêu cầu hủy";
            case "CANCELLED":
                return "Đã hủy";
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

    public List<Order> getOrdersByMaKH(long maKH) {
        return orderDAO.getOrdersByMaKH(maKH);
    }

    public Order getOrderByMaDH(long maDH, long maKH) {
        return orderDAO.getOrderByMaDH(maDH, maKH);
    }

    public List<OrderItem> getOrderItemsByMaDH(long maDH) {
        return orderDAO.getOrderItemsByMaDH(maDH);
    }

    public boolean requestCancelOrder(long maDH, long maKH) {
        Order dh = orderDAO.getOrderByMaDH(maDH, maKH);
        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = dh.getTrangThaiDon() == null ? "" : dh.getTrangThaiDon().trim().toUpperCase();
        if ("PENDING".equals(status)) {
            return orderDAO.updateOrderStatus(maDH, "CANCELLED");
        }

        if ("CONFIRMED".equals(status)) {
            return orderDAO.updateOrderStatus(maDH, "CANCEL_REQUESTED");
        }

        System.out.println("Đơn hàng ở trạng thái " + status + " nên không thể hủy.");
        return false;
    }

    public boolean confirmReceived(long maDH, long maKH) {
        Order dh = orderDAO.getOrderByMaDH(maDH, maKH);
        if (dh == null) {
            System.out.println("Không tìm thấy đơn hàng hoặc đơn không thuộc khách hàng này.");
            return false;
        }

        String status = dh.getTrangThaiDon() == null ? "" : dh.getTrangThaiDon().trim().toUpperCase();
        if (!"SHIPPING".equals(status)) {
            System.out.println("Chỉ có thể xác nhận hàng khi đơn đang giao.");
            return false;
        }

        return orderDAO.updateOrderStatus(maDH, "DELIVERED");
    }

    public boolean canReviewOrder(long maDH, long maKH) {
        Order dh = orderDAO.getOrderByMaDH(maDH, maKH);
        if (dh == null) {
            return false;
        }

        String status = dh.getTrangThaiDon() == null ? "" : dh.getTrangThaiDon().trim().toUpperCase();
        return "DELIVERED".equals(status);
    }

    public List<Order> getOrdersForStaff(String status, String keyword, String fromDate, String toDate) {
        return orderDAO.getOrdersForStaff(status, keyword, fromDate, toDate);
    }

    public Order getOrderByIdForStaff(long orderId) {
        return orderDAO.getOrderByIdForStaff(orderId);
    }

    public Map<String, Object> getOrderByIdForStaffWithDemo(long orderId, String mode, long delayMs) throws SQLException {
        return orderDAO.getOrderByIdForStaffWithDemo(orderId, mode, delayMs);
    }

    public List<OrderStatusHistoryResponse> getOrderStatusHistory(long maDH) {
        List<OrderStatusHistory> history = orderDAO.getOrderStatusHistory(maDH);
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

    public List<OrderStatusHistoryResponse> getOrderStatusHistoryForCustomer(long maDH, long maKH) {
        Order order = orderDAO.getOrderByMaDH(maDH, maKH);
        if (order == null) {
            return null;
        }

        return getOrderStatusHistory(maDH);
    }

    public boolean updateOrderStatusByStaff(long maDH, long staffId, String nextStatus, String cancelReason) {
        Order order = orderDAO.getOrderByIdForStaff(maDH);
        if (order == null) {
            System.out.println("[STAFF UPDATE] Không tìm thấy đơn hàng.");
            return false;
        }

        String currentStatus = order.getTrangThaiDon() == null ? "" : order.getTrangThaiDon().trim().toUpperCase();
        String normalizedNextStatus = nextStatus == null ? "" : nextStatus.trim().toUpperCase();

        System.out.println("[STAFF UPDATE] maDH=" + maDH);
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
            result = orderDAO.updateOrderStatusStaffAndCancelReason(maDH, staffId, normalizedNextStatus, cancelReason);
        } else {
            result = orderDAO.updateOrderStatusAndStaff(maDH, staffId, normalizedNextStatus);
        }

        System.out.println("[STAFF UPDATE] result=" + result);
        return result;
    }

    public Map<String, Object> countOrderStatsTwiceForPhantomReadDemo(
            String isolation, long delayMs) throws SQLException {
        String isolationLabel = "SERIALIZABLE".equalsIgnoreCase(isolation) ? "SERIALIZABLE" : "READ_COMMITTED";
        return orderDAO.countOrderStatsTwiceForPhantomReadDemo(isolationLabel, delayMs);
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
