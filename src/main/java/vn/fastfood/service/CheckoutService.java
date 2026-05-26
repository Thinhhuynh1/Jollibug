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
        List<String> parts = new ArrayList<>();

        if (hasText(request.getGhiChu())) {
            parts.add(request.getGhiChu().trim());
        }

        if (hasText(request.getDeliveryName())) {
            parts.add("NgÆ°á»i nháº­n: " + request.getDeliveryName().trim());
        }

        if (hasText(request.getDeliveryPhone())) {
            String phone = request.getDeliveryPhone().trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("Sá»‘ Ä‘iá»‡n thoáº¡i nháº­n hÃ ng khÃ´ng há»£p lá»‡.");
            }
            parts.add("SÄT: " + phone);
        }

        if (hasText(request.getEmail())) {
            String email = request.getEmail().trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Email khÃ´ng há»£p lá»‡.");
            }
            parts.add("Email: " + email);
        }

        if (hasText(request.getDeliveryAddress())) {
            parts.add("Äá»‹a chá»‰ nháº­p: " + request.getDeliveryAddress().trim());
        }

        if (parts.isEmpty()) {
            throw new IllegalArgumentException("Vui lÃ²ng nháº­p thÃ´ng tin giao hÃ ng há»£p lá»‡.");
        }

        return String.join("; ", parts);
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
            return new CheckoutResponse(false, "Dá»¯ liá»‡u Ä‘áº·t hÃ ng khÃ´ng há»£p lá»‡.", null, null, null, null);
        }

        if (session == null) {
            return new CheckoutResponse(false, "PhiÃªn lÃ m viá»‡c khÃ´ng há»£p lá»‡. Vui lÃ²ng Ä‘Äƒng nháº­p láº¡i.", null, null, null, null);
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
            return new CheckoutResponse(false, "KhÃ´ng tÃ¬m tháº¥y thÃ´ng tin tÃ i khoáº£n. Vui lÃ²ng Ä‘Äƒng nháº­p láº¡i.", null, null, null, null);
        }

        Long maDC = request.getMaDC();
        if (maDC != null && maDC <= 0) {
            maDC = null;
        }

        if (maDC != null && !checkoutDAO.isValidAddress(maKH, maDC)) {
            return new CheckoutResponse(false, "Äá»‹a chá»‰ giao hÃ ng khÃ´ng há»£p lá»‡ hoáº·c khÃ´ng thuá»™c tÃ i khoáº£n hiá»‡n táº¡i.", null, null, null, null);
        }

        if (maDC == null && !hasText(request.getDeliveryAddress())) {
            return new CheckoutResponse(false, "Vui lÃ²ng nháº­p Ä‘á»‹a chá»‰ giao hÃ ng.", null, null, null, null);
        }

        String maPT = request.getMaPT();
        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lÃ²ng chá»n phÆ°Æ¡ng thá»©c thanh toÃ¡n.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();
        if (!checkoutDAO.isValidPTTT(maPT)) {
            return new CheckoutResponse(false, "PhÆ°Æ¡ng thá»©c thanh toÃ¡n khÃ´ng há»£p lá»‡.", null, null, null, null);
        }

        List<CheckoutCartItem> items = getCheckoutItemsFromSession(session);
        if (items.isEmpty()) {
            return new CheckoutResponse(false, "Giá» hÃ ng Ä‘ang trá»‘ng.", null, null, null, null);
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

        String orderNote;
        try {
            orderNote = buildGhiChu(request);
        } catch (IllegalArgumentException e) {
            return new CheckoutResponse(false, e.getMessage(), null, subtotal, discountTotal, total);
        }

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
                    items
            );

            session.removeAttribute("cart");
            return new CheckoutResponse(true, "Äáº·t hÃ ng thÃ nh cÃ´ng.", orderId, subtotal, discountTotal, total);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CheckoutResponse(false, "Äáº·t hÃ ng tháº¥t báº¡i do lá»—i há»‡ thá»‘ng.", null, subtotal, discountTotal, total);
        }
    }
}
