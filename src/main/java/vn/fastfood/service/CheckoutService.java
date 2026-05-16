package vn.fastfood.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import vn.fastfood.dao.CartDAO;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.model.CheckoutCartItem;

public class CheckoutService {
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public CheckoutResponse checkout(CheckoutRequest request) {
        if (request == null || request.getCustomerId() <= 0) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        long customerId = request.getCustomerId();

        if (!isValidDeliveryInfo(request)) {
            return new CheckoutResponse(false, "Thông tin giao hàng không hợp lệ.", null, null, null, null);
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
        Long selectedAddressId = checkoutDAO.isValidAddress(customerId, request.getMaDC())
                ? request.getMaDC()
                : null;
        String orderNote = buildOrderNote(request);

        try {
            long orderId = checkoutDAO.createOrder(
                    customerId,
                    selectedAddressId,
                    subtotal,
                    discountAmount,
                    total,
                    maGG,
                    orderNote
            );

            boolean historyRecorded = orderDAO.insertOrderStatusHistory(
                    orderId,
                    null,
                    "PENDING",
                    "CUSTOMER",
                    customerId,
                    null
            );

            if (!historyRecorded) {
                System.out.println("[ORDER HISTORY] Could not record initial PENDING history for orderId=" + orderId);
            }

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

    private boolean isValidDeliveryInfo(CheckoutRequest request) {
        if (isBlank(request.getDeliveryName()) || request.getDeliveryName().trim().length() < 2) {
            return false;
        }

        if (isBlank(request.getDeliveryAddress()) || request.getDeliveryAddress().trim().length() < 10) {
            return false;
        }

        String phone = request.getDeliveryPhone();
        if (isBlank(phone) || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return false;
        }

        String email = request.getEmail();
        return isBlank(email) || EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private String buildOrderNote(CheckoutRequest request) {
        StringBuilder note = new StringBuilder();
        note.append("Người nhận: ").append(safeTrim(request.getDeliveryName()))
                .append("; SĐT: ").append(safeTrim(request.getDeliveryPhone()))
                .append("; Email: ").append(safeTrim(request.getEmail()))
                .append("; Địa chỉ giao: ").append(safeTrim(request.getDeliveryAddress()));

        if (!isBlank(request.getGhiChu())) {
            note.append("; Ghi chú: ").append(request.getGhiChu().trim());
        }

        return note.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
