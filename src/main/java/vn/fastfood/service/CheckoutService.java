package vn.fastfood.service;

import vn.fastfood.dao.CartDAO;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.model.CheckoutCartItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CheckoutService {
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final CartDAO cartDAO = new CartDAO();

    public CheckoutResponse checkout(CheckoutRequest request) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        long customerId = request.getCustomerId();

        if (!checkoutDAO.isValidAddress(customerId, request.getMaDC())) {
            return new CheckoutResponse(false, "Địa chỉ giao hàng không hợp lệ.", null, null, null, null);
        }

        String maPT = request.getMaPT();

        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lòng chọn phương thức thanh toán.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();

        if (!checkoutDAO.isValidPaymentMethod(maPT)) {
            return new CheckoutResponse(false, "Phương thức thanh toán không hợp lệ.", null, null, null, null);
        }

        List<CheckoutCartItem> items = checkoutDAO.getCheckoutItems(customerId);

        if (items.isEmpty()) {
            return new CheckoutResponse(false, "Giỏ hàng đang trống.", null, null, null, null);
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CheckoutCartItem item : items) {
            subtotal = subtotal.add(item.getThanhTien());
        }

        BigDecimal discountAmount = checkoutDAO.calculateDiscount(request.getDiscountCode(), subtotal);

        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }

        BigDecimal total = subtotal.subtract(discountAmount);

        Long maGG = checkoutDAO.findDiscountIdByCode(request.getDiscountCode());

        try {
            long orderId = checkoutDAO.createOrder(
                    customerId,
                    request.getMaDC(),
                    subtotal,
                    discountAmount,
                    total,
                    maGG,
                    request.getGhiChu()
            );

            for (CheckoutCartItem item : items) {
                checkoutDAO.createOrderItem(orderId, item);
            }

            checkoutDAO.createPayment(orderId, maPT, total);

            cartDAO.clearCart(customerId);

            return new CheckoutResponse(
                    true,
                    "Đặt hàng thành công.",
                    orderId,
                    subtotal,
                    discountAmount,
                    total
            );

        } catch (SQLException e) {
            e.printStackTrace();

            return new CheckoutResponse(
                    false,
                    "Đặt hàng thất bại do lỗi hệ thống.",
                    null,
                    subtotal,
                    discountAmount,
                    total
            );
        }
    }
}