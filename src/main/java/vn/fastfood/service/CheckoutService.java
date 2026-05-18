package vn.fastfood.service;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.model.CartItem;
import vn.fastfood.model.CheckoutCartItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutService {

    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        if (session == null) {
            return new CheckoutResponse(false, "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        long customerId = request.getCustomerId();

        if (customerId <= 0) {
            return new CheckoutResponse(false, "Thông tin khách hàng không hợp lệ. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        /*
         * Địa chỉ giao hàng phải đến từ DIACHI/MaDC.
         * GhiChu chỉ lưu ghi chú khách nhập, không lưu người nhận/SĐT/email/địa chỉ.
         */
        Long maDC = request.getMaDC();

        if (maDC != null && maDC <= 0) {
            maDC = null;
        }

        if (maDC == null) {
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
            if (item.getThanhTien() != null) {
                subtotal = subtotal.add(item.getThanhTien());
            }
        }

        BigDecimal discountAmount = checkoutDAO.calculateDiscount(request.getDiscountCode(), subtotal);

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }

        BigDecimal total = subtotal.subtract(discountAmount);
        Long maGG = checkoutDAO.findDiscountIdByCode(request.getDiscountCode());

        try {
            long orderId = checkoutDAO.createOrder(
                    customerId,
                    maDC,
                    subtotal,
                    discountAmount,
                    total,
                    maGG,
                    normalizeNote(request.getGhiChu())
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
                    total
            );
        }
    }

    private List<CheckoutCartItem> getCheckoutItemsFromSession(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj == null) {
            return new ArrayList<>();
        }

        if (!(cartObj instanceof List<?> rawCart)) {
            return new ArrayList<>();
        }

        List<CheckoutCartItem> checkoutItems = new ArrayList<>();

        for (Object obj : rawCart) {
            if (!(obj instanceof CartItem cartItem)) {
                continue;
            }

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

            BigDecimal thanhTien = cartItem.getDonGia()
                    .multiply(BigDecimal.valueOf(cartItem.getSoLuong()));

            item.setThanhTien(thanhTien);

            checkoutItems.add(item);
        }

        return checkoutItems;
    }

    private String normalizeNote(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}