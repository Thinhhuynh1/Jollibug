package vn.fastfood.service;

import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.model.CartItem;
import vn.fastfood.model.CheckoutCartItem;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CheckoutService {
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        if (session == null) {
            return new CheckoutResponse(false, "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        long customerId = request.getCustomerId();

        /*
         * Không bắt buộc địa chỉ phải tồn tại trong bảng DIACHI.
         * Nếu khách chọn địa chỉ đã lưu và MaDC hợp lệ, dùng MaDC đó.
         * Nếu MaDC không hợp lệ hoặc khách nhập địa chỉ mới, vẫn cho đặt hàng,
         * thông tin giao hàng sẽ được lưu trong GhiChu.
         */
        Long maDC = request.getMaDC();

        if (maDC != null && maDC <= 0) {
            maDC = null;
        }

        if (maDC == null || maDC <= 0) {
            return new CheckoutResponse(false, "Vui lòng chọn địa chỉ giao hàng.", null, null, null, null);
        }

        if (!checkoutDAO.isValidAddress(customerId, maDC)) {
            return new CheckoutResponse(false, "Địa chỉ giao hàng không hợp lệ hoặc không thuộc tài khoản hiện tại.", null, null, null, null);
        }

        String maPT = request.getMaPT();
        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lòng chọn phương thức thanh toán.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();

        if (!checkoutDAO.isValidPaymentMethod(maPT)) {
            return new CheckoutResponse(false, "Phương thức thanh toán không hợp lệ.", null, null, null, null);
        }

        List<CheckoutCartItem> items = getCheckoutItemsFromSession(session);

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
        String orderNote = buildOrderNote(request);

        try {
            long orderId = checkoutDAO.createOrder(
                    customerId,
                    maDC,
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
                    null);

            if (!historyRecorded) {
                System.out.println("[ORDER HISTORY] Could not record initial PENDING history for orderId=" + orderId);
            }

            for (CheckoutCartItem item : items) {
                checkoutDAO.createOrderItem(orderId, item);
            }

            checkoutDAO.createPayment(orderId, maPT, total);

            session.removeAttribute("cart");

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
                    total);
        }
    }

    private List<CheckoutCartItem> getCheckoutItemsFromSession(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj == null) {
            return new ArrayList<>();
        }

        if (!(cartObj instanceof List<?>)) {
            return new ArrayList<>();
        }

        List<?> rawCart = (List<?>) cartObj;
        List<CheckoutCartItem> checkoutItems = new ArrayList<>();

        for (Object obj : rawCart) {
            if (!(obj instanceof CartItem)) {
                continue;
            }

            CartItem cartItem = (CartItem) obj;

            if (cartItem.getSoLuong() <= 0) {
                continue;
            }

            if (cartItem.getDonGia() == null) {
                continue;
            }

            CheckoutCartItem item = new CheckoutCartItem();

            item.setMaMon(cartItem.getMaMon());
            item.setTenMon(cartItem.getTenMon());
            item.setSoLuong(cartItem.getSoLuong());
            item.setDonGia(cartItem.getDonGia());

            BigDecimal thanhTien = cartItem.getDonGia().multiply(BigDecimal.valueOf(cartItem.getSoLuong()));
            item.setThanhTien(thanhTien);

            checkoutItems.add(item);
        }

        return checkoutItems;
    }

    private String buildOrderNote(CheckoutRequest request) {
        List<String> parts = new ArrayList<>();

        if (hasText(request.getGhiChu())) {
            parts.add(request.getGhiChu().trim());
        }

        if (hasText(request.getDeliveryName())) {
            parts.add("Người nhận: " + request.getDeliveryName().trim());
        }

        if (hasText(request.getDeliveryPhone())) {
            String phone = request.getDeliveryPhone().trim();

            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("Số điện thoại nhận hàng không hợp lệ.");
            }

            parts.add("SĐT: " + phone);
        }

        if (hasText(request.getEmail())) {
            String email = request.getEmail().trim();

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Email không hợp lệ.");
            }

            parts.add("Email: " + email);
        }

        if (hasText(request.getDeliveryAddress())) {
            parts.add("Địa chỉ nhập: " + request.getDeliveryAddress().trim());
        }

        return String.join("; ", parts);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
