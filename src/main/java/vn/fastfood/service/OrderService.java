package vn.fastfood.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import vn.fastfood.config.DBConnection;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dao.PaymentDAO;
import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

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
            return false;
        }

        String status = dh.getTrangThaiDon() == null ? "" : dh.getTrangThaiDon().trim().toUpperCase();
        if (orderDAO.canChangeOrderStatus(status, "CANCELLED")) {
            return orderDAO.updateOrderStatus(maDH, "CANCELLED");
        }

        return false;
    }

    public boolean confirmReceived(long maDH, long maKH) {
        Order dh = orderDAO.getOrderByMaDH(maDH, maKH);
        if (dh == null) {
            return false;
        }

        String status = dh.getTrangThaiDon() == null ? "" : dh.getTrangThaiDon().trim().toUpperCase();
        if (!orderDAO.canChangeOrderStatus(status, "DELIVERED")) {
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

    public Order getOrderByMaDHForStaff(long maDH) {
        return orderDAO.getOrderByMaDHForStaff(maDH);
    }

    public boolean updateOrderStatusByStaff(long maDH, long staffId, String nextStatus, String cancelReason) {
        Order order = orderDAO.getOrderByMaDHForStaff(maDH);
        if (order == null) {
            return false;
        }

        String currentStatus = order.getTrangThaiDon() == null ? "" : order.getTrangThaiDon().trim().toUpperCase();
        if (!orderDAO.canChangeOrderStatus(currentStatus, nextStatus)) {
            return false;
        }

        if ("CANCELLED".equals(nextStatus)) {
            return orderDAO.updateOrderStatusStaffAndCancelReason(maDH, staffId, nextStatus, cancelReason);
        }

        String paymentMethod = order.getMaPT() == null ? "" : order.getMaPT().trim().toUpperCase();
        String paymentStatus = order.getTrangThaiTT() == null ? "" : order.getTrangThaiTT().trim().toUpperCase();
        if ("DELIVERED".equals(nextStatus) && "COD".equals(paymentMethod) && !"PAID".equals(paymentStatus)) {
            return updateDeliveredCodTransaction(maDH, staffId, nextStatus);
        }

        return orderDAO.updateOrderStatusAndStaff(maDH, staffId, nextStatus);
    }

    private boolean updateDeliveredCodTransaction(long maDH, long staffId, String nextStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                orderDAO.lockOrder(conn, maDH);
                paymentDAO.lockPayment(conn, maDH);
                orderDAO.updateOrderStatusAndStaff(conn, maDH, staffId, nextStatus);
                paymentDAO.updatePaymentStatus(conn, maDH, "PAID");
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setTransactionIsolation(originalIsolation);
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
