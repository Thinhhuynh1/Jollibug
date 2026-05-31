package vn.fastfood.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dao.CartDAO;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.MaGiamGia;
import vn.fastfood.model.CartItem;
import vn.fastfood.model.CheckoutCartItem;
import vn.fastfood.repository.AddressRepository;

@Service
public class CheckoutService {
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final AddressRepository addressRepository;
    private final JpaOrderService jpaOrderService;
    private final CouponService couponService;

    public CheckoutService(AddressRepository addressRepository, JpaOrderService jpaOrderService,
            CouponService couponService) {
        this.addressRepository = addressRepository;
        this.jpaOrderService = jpaOrderService;
        this.couponService = couponService;
    }

    public List<CheckoutCartItem> getCheckoutItems(long customerId, HttpSession session) {
        List<CheckoutCartItem> items = checkoutDAO.getCheckoutItems(customerId);
        if (!items.isEmpty()) {
            return items;
        }
        return getSessionCheckoutItems(session);
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        long customerId = request.getCustomerId();
        DiaChi diaChi = resolveAddress(customerId, request.getMaDC());
        if (diaChi == null) {
            return new CheckoutResponse(false,
                    "Địa chỉ giao hàng không hợp lệ. Vui lòng chọn địa chỉ đã lưu hoặc thêm địa chỉ mới.",
                    null, null, null, null);
        }

        String maPT = request.getMaPT();
        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lòng chọn phương thức thanh toán.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();
        if (!JpaOrderService.isSupportedPaymentMethod(maPT)) {
            return new CheckoutResponse(false, "Phương thức thanh toán không hợp lệ.", null, null, null, null);
        }

        List<CheckoutCartItem> items = getCheckoutItems(customerId, session);
        if (items.isEmpty()) {
            return new CheckoutResponse(false, "Giỏ hàng đang trống.", null, null, null, null);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CheckoutCartItem item : items) {
            subtotal = subtotal.add(item.getThanhTien());
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountCode = request.getDiscountCode();
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            Optional<MaGiamGia> couponOpt = couponService.findValidCoupon(discountCode);
            if (couponOpt.isEmpty()) {
                return new CheckoutResponse(false, "Mã giảm giá không hợp lệ hoặc đã hết hạn.", null, null, null, null);
            }

            MaGiamGia coupon = couponOpt.get();
            double subtotalValue = subtotal.doubleValue();
            if (!couponService.meetsMinimumOrder(coupon, subtotalValue)) {
                return new CheckoutResponse(false, couponService.getMinimumOrderMessage(coupon), null, null, null, null);
            }

            discountAmount = BigDecimal.valueOf(couponService.calculateDiscount(coupon, subtotalValue));
            if (discountAmount.compareTo(subtotal) > 0) {
                discountAmount = subtotal;
            }
        }

        BigDecimal total = subtotal.subtract(discountAmount);
        String ghiChu = JpaOrderService.buildCheckoutNote(request.getGhiChu(), maPT, discountAmount);

        try {
            long orderId = jpaOrderService.createOrder(customerId, diaChi, total, ghiChu, items);

            cartDAO.clearCart(customerId);
            if (session != null) {
                session.removeAttribute("cart");
            }

            return new CheckoutResponse(
                    true,
                    "Đặt hàng thành công.",
                    orderId,
                    subtotal,
                    discountAmount,
                    total);

        } catch (Exception e) {
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

    private DiaChi resolveAddress(long customerId, Long maDC) {
        if (maDC == null) {
            return null;
        }
        DiaChi diaChi = addressRepository.findByMaDC(maDC);
        if (diaChi == null || diaChi.getUser() == null || diaChi.getUser().getMaTK() != customerId) {
            return null;
        }
        return diaChi;
    }

    private List<CheckoutCartItem> getSessionCheckoutItems(HttpSession session) {
        if (session == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
        if (sessionCart == null || sessionCart.isEmpty()) {
            return List.of();
        }

        List<CheckoutCartItem> items = new ArrayList<>();
        for (CartItem cartItem : sessionCart) {
            CheckoutCartItem item = new CheckoutCartItem();
            item.setMaMon(cartItem.getMaMon());
            item.setTenMon(cartItem.getTenMon());
            item.setSoLuong(cartItem.getSoLuong());
            item.setDonGia(cartItem.getDonGia());
            item.setThanhTien(cartItem.getThanhTien());
            items.add(item);
        }
        return items;
    }
}
