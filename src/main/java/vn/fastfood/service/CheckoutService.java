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
    private final MaGiamGiaService maGiamGiaService;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public CheckoutService(MaGiamGiaService maGiamGiaService) {
        this.checkoutDAO = new CheckoutDAO();
        this.maGiamGiaService = maGiamGiaService;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String buildGhiChu(CheckoutRequest request) {
        return hasText(request.getGhiChu()) ? request.getGhiChu().trim() : null;
    }

    private void validateDeliveryInfo(CheckoutRequest request, Long maDC) {
        if (maDC == null && (!hasText(request.getDeliveryName())
                || !hasText(request.getDeliveryPhone())
                || !hasText(request.getDeliveryAddress()))) {
            throw new IllegalArgumentException("Vui lòng nhập thông tin giao hàng hợp lệ.");
        }

        if (hasText(request.getDeliveryPhone())) {
            String phone = request.getDeliveryPhone().trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("Số điện thoại nhận hàng không hợp lệ.");
            }
        }

        if (hasText(request.getEmail())) {
            String email = request.getEmail().trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Email không hợp lệ.");
            }
        }
    }

    private List<CheckoutCartItem> getCheckoutItemsFromSession(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

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

            CheckoutCartItem item = new CheckoutCartItem();
            item.setMaMon(cartItem.getMaMon());
            item.setTenMon(cartItem.getTenMon());
            item.setSoLuong(cartItem.getSoLuong());
            item.setDonGia(cartItem.getDonGia());
            item.setThanhTien(cartItem.getDonGia() * cartItem.getSoLuong());
            checkoutItems.add(item);
        }

        return checkoutItems;
    }

    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        if (session == null) {
            return new CheckoutResponse(false, "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        Long maKH = null;
        Object userObj = session.getAttribute("user");
        if (userObj instanceof User user) {
            maKH = user.getMaTK();
        } else {
            Object maTKObj = session.getAttribute("userId");
            if (maTKObj instanceof Number maTKNumber) {
                maKH = maTKNumber.longValue();
            }
        }

        if (maKH == null || maKH <= 0) {
            return new CheckoutResponse(false, "Không tìm thấy thông tin tài khoản. Vui lòng đăng nhập lại.", null, null, null, null);
        }

        Long maDC = request.getMaDC();
        if (maDC != null && maDC <= 0) {
            maDC = null;
        }

        if (maDC != null && !checkoutDAO.isValidAddress(maKH, maDC)) {
            return new CheckoutResponse(false, "Địa chỉ giao hàng không hợp lệ hoặc không thuộc tài khoản hiện tại.", null, null, null, null);
        }

        try {
            validateDeliveryInfo(request, maDC);
        } catch (IllegalArgumentException e) {
            return new CheckoutResponse(false, e.getMessage(), null, null, null, null);
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
            subtotal += item.getThanhTien();
        }

        MaGiamGia coupon = maGiamGiaService.findValidCoupon(request.getDiscountCode()).orElse(null);
        double discountTotal = maGiamGiaService.calculateDiscount(coupon, subtotal);
        if (discountTotal > subtotal) {
            discountTotal = subtotal;
        }

        double total = subtotal - discountTotal;
        Long maGG = coupon != null ? coupon.getMaGG() : null;
        String orderNote = buildGhiChu(request);

        try {
            long orderId = checkoutDAO.checkout(
                    maKH,
                    maDC,
                    subtotal,
                    discountTotal,
                    total,
                    maPT,
                    maGG,
                    orderNote,
                    request.getDeliveryName(),
                    request.getDeliveryPhone(),
                    request.getEmail(),
                    request.getDeliveryAddress(),
                    items
            );

            session.removeAttribute("cart");
            return new CheckoutResponse(true, "Đặt hàng thành công.", orderId, subtotal, discountTotal, total);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CheckoutResponse(false, "Đặt hàng thất bại do lỗi hệ thống.", null, subtotal, discountTotal, total);
        }
    }
}
