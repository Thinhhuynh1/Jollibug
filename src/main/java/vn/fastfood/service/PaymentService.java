package vn.fastfood.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import vn.fastfood.config.DBConnection;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dao.PaymentDAO;
import vn.fastfood.model.Order;
import vn.fastfood.model.Payment;

@Service
public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    public Payment getPaymentByMaDH(long maDH) {
        return paymentDAO.getPaymentByOrderId(maDH);
    }

    public Payment getOrCreateDefaultPayment(long maDH, String maPT) {
        Payment thanhToan = paymentDAO.getPaymentByOrderId(maDH);
        if (thanhToan != null) {
            return thanhToan;
        }

        Order donHang = orderDAO.getOrderByMaDHForStaff(maDH);
        if (donHang == null || donHang.getThanhTien() == null) {
            return null;
        }

        String phuongThucThanhToan = "";
        if (maPT != null) {
            phuongThucThanhToan = maPT.trim().toUpperCase();
        }

        if (phuongThucThanhToan.isEmpty()) {
            if (donHang.getMaPT() != null) {
                phuongThucThanhToan = donHang.getMaPT().trim().toUpperCase();
            }
        }

        if (phuongThucThanhToan.isEmpty()) {
            phuongThucThanhToan = "COD";
        }

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                orderDAO.lockOrder(conn, maDH);

                Payment existingPayment = paymentDAO.getPaymentByOrderId(maDH);
                if (existingPayment == null) {
                    paymentDAO.createPayment(
                            conn,
                            maDH,
                            phuongThucThanhToan,
                            donHang.getThanhTien().doubleValue(),
                            "PENDING"
                    );
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return null;
            } finally {
                conn.setTransactionIsolation(originalIsolation);
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return paymentDAO.getPaymentByOrderId(maDH);
    }

    public boolean createPayment(long maDH, String maPT, double soTien) {
        if (maPT == null || maPT.trim().isEmpty() || soTien < 0) {
            return false;
        }

        return paymentDAO.createPayment(maDH, maPT.trim().toUpperCase(), soTien, "PENDING");
    }

    public boolean confirmPayment(long maDH) {
        return confirmPaymentWithTransaction(maDH);
    }

    public boolean confirmPaymentWithTransaction(long maDH) {
        Payment thanhToan = paymentDAO.getPaymentByOrderId(maDH);
        if (thanhToan == null) {
            return false;
        }

        if ("PAID".equalsIgnoreCase(thanhToan.getTrangThaiTT())) {
            return true;
        }

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                orderDAO.lockOrder(conn, maDH);
                paymentDAO.lockPayment(conn, maDH);

                paymentDAO.updatePaymentStatus(conn, maDH, "PAID");
                orderDAO.updateOrderStatus(conn, maDH, "CONFIRMED");

                conn.commit();
                conn.setTransactionIsolation(originalIsolation);
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                conn.setTransactionIsolation(originalIsolation);
                conn.setAutoCommit(originalAutoCommit);
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean failPayment(long maDH) {
        Payment thanhToan = paymentDAO.getPaymentByOrderId(maDH);
        if (thanhToan == null || "PAID".equalsIgnoreCase(thanhToan.getTrangThaiTT())) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            int originalIsolation = conn.getTransactionIsolation();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try {
                paymentDAO.lockPayment(conn, maDH);
                paymentDAO.updatePaymentStatus(conn, maDH, "FAILED");
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
