package vn.fastfood.service;

import vn.fastfood.dao.PaymentDAO;
import vn.fastfood.model.Payment;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public Payment getPaymentByOrderId(long orderId) {
        return paymentDAO.getPaymentByOrderId(orderId);
    }

    public boolean confirmPayment(long orderId) {
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);

        if (payment == null) {
            return false;
        }

        if ("Paid".equalsIgnoreCase(payment.getTrangThaiTT())) {
            return true;
        }

        return paymentDAO.updatePaymentStatus(orderId, "Paid");
    }

    public boolean failPayment(long orderId) {
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);

        if (payment == null) {
            return false;
        }

        if ("Paid".equalsIgnoreCase(payment.getTrangThaiTT())) {
            return false;
        }

        return paymentDAO.updatePaymentStatus(orderId, "Failed");
    }
}
