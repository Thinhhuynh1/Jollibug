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

        String phuongThucThanhToan = normalizeMethod(maPT);
        if (phuongThucThanhToan.isEmpty()) {
            phuongThucThanhToan = normalizeMethod(donHang.getMaPT());
        }
        if (phuongThucThanhToan.isEmpty()) {
            phuongThucThanhToan = "COD";
        }

        paymentDAO.createPayment(
                maDH,
                phuongThucThanhToan,
                donHang.getThanhTien().doubleValue(),
                "PENDING"
        );

        return paymentDAO.getPaymentByOrderId(maDH);
    }

    public boolean createPayment(long maDH, String maPT, double soTien) {
        if (maPT == null || maPT.trim().isEmpty() || soTien < 0) {
            return false;
        }

        return paymentDAO.createPayment(maDH, maPT.trim().toUpperCase(), soTien, "PENDING");
    }

    public boolean confirmPayment(long maDH) {
        Payment thanhToan = paymentDAO.getPaymentByOrderId(maDH);
        if (thanhToan == null) {
            return false;
        }

        if (isPaid(thanhToan)) {
            return true;
        }

        return paymentDAO.updatePaymentStatus(maDH, "PAID");
    }

    public boolean confirmPaymentWithTransaction(long maDH) {
        Payment thanhToan = paymentDAO.getPaymentByOrderId(maDH);
        if (thanhToan == null) {
            return false;
        }

        if (isPaid(thanhToan)) {
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
        if (thanhToan == null || isPaid(thanhToan)) {
            return false;
        }

        return paymentDAO.updatePaymentStatus(maDH, "FAILED");
    }

    private boolean isPaid(Payment thanhToan) {
        return "PAID".equalsIgnoreCase(thanhToan.getTrangThaiTT());
    }

    private String normalizeMethod(String method) {
        return method == null ? "" : method.trim().toUpperCase();
    }
}
