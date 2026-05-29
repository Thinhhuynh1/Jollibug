package vn.fastfood.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.entity.User;
import vn.fastfood.model.CartItem;
import vn.fastfood.model.CheckoutCartItem;

@Service
public class CheckoutService {
    private final CheckoutDAO checkoutDAO;
    private final CouponService couponService;
    private final PaymentService paymentService = new PaymentService();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public CheckoutService(CouponService couponService) {
        this.checkoutDAO = new CheckoutDAO();
        this.couponService = couponService;
    }

    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        if (session == null) {
            return new CheckoutResponse(false, "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        Long customerId = resolveCustomerId(session);
        if (customerId == null || customerId <= 0) {
            return new CheckoutResponse(false, "Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        Long maDC = request.getMaDC();
        if (maDC != null && maDC <= 0) {
            maDC = null;
        }

        if (maDC != null && !checkoutDAO.isValidAddress(customerId, maDC)) {
            return new CheckoutResponse(false, "Địa chỉ giao hàng không hợp lệ hoặc không thuộc tài khoản hiện tại.", null, null, null, null);
        }

        if (maDC == null && !hasText(request.getDeliveryAddress())) {
            return new CheckoutResponse(false, "Vui lòng nhập địa chỉ giao hàng.", null, null, null, null);
        }

        String maPT = request.getMaPT();
        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lòng chọn phương thức thanh toán.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();
        if (!checkoutDAO.isValidPTTT(maPT)) {
            return new CheckoutResponse(false, "Phương thức thanh toán không hợp lệ.", null, null, null, null);
        }

        List<CheckoutCartItem> items = getCheckoutItemsFromSession(session);
        if (items.isEmpty()) {
            return new CheckoutResponse(false, "Giỏ hàng đang trống.", null, null, null, null);
        }

        double subtotal = 0;
        for (CheckoutCartItem item : items) {
            subtotal += checkoutDAO.calcSubtotal(item.getDonGia(), item.getSoLuong());
        }

        MaGiamGia coupon = couponService.findValidCoupon(request.getDiscountCode()).orElse(null);
        double discountTotal = couponService.calculateDiscount(coupon, subtotal);
        if (discountTotal > subtotal) {
            discountTotal = subtotal;
        }

        double total = subtotal - discountTotal;
        Long maGG = coupon != null ? coupon.getMaGG() : null;

        String orderNote;
        try {
            orderNote = buildOrderNote(request);
        } catch (IllegalArgumentException e) {
            return new CheckoutResponse(
                false, 
                e.getMessage(), 
                null, 
                subtotal, 
                discountTotal, 
                total
            );
        }

        try {
            long orderId = checkoutDAO.createOrderWithItemsAndPayment(
                    customerId,
                    maDC,
                    subtotal,
                    discountTotal,
                    total,
                    maGG,
                    orderNote,
                    items,
                    maPT
            );
            if ("COD".equalsIgnoreCase(maPT)) {
                paymentService.confirmPayment(orderId);
            }

            session.removeAttribute("cart");

            return new CheckoutResponse(
                    true,
                    "Đặt hàng thành công.",
                    orderId,
                    subtotal,
                    discountTotal,
                    total
            );
        } catch (SQLException e) {
            e.printStackTrace();

            return new CheckoutResponse(
                    false,
                    "Đặt hàng thất bại do lỗi hệ thống.",
                    null,
                    subtotal,
                    discountTotal,
                    total
            );
        }
    }

    private List<CheckoutCartItem> getCheckoutItemsFromSession(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj == null || !(cartObj instanceof List<?>)) {
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

            CheckoutCartItem item = new CheckoutCartItem();
            item.setMaMon(cartItem.getMaMon());
            item.setTenMon(cartItem.getTenMon());
            item.setSoLuong(cartItem.getSoLuong());
            item.setDonGia(cartItem.getDonGia());
            item.setThanhTien(checkoutDAO.calcSubtotal(cartItem.getDonGia(), cartItem.getSoLuong()));
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

        if (parts.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập thông tin giao hàng hợp lệ.");
        }

        return String.join("; ", parts);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Long resolveCustomerId(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            return user.getMaTK();
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Number userIdNumber) {
            return userIdNumber.longValue();
        }

        return null;
    }
}
