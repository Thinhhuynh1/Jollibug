package vn.fastfood.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.dao.CartDAO;
import vn.fastfood.dao.CheckoutDAO;
import vn.fastfood.dao.OrderDAO;
import vn.fastfood.dto.CheckoutRequest;
import vn.fastfood.dto.CheckoutResponse;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.model.CartItem;
import vn.fastfood.model.CheckoutCartItem;
import vn.fastfood.repository.AddressRepository;

@Service
public class CheckoutService {
    private final CheckoutDAO checkoutDAO = new CheckoutDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final AddressRepository addressRepository;

    public CheckoutService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<CheckoutCartItem> getCheckoutItems(long customerId, HttpSession session) {
        List<CheckoutCartItem> items = checkoutDAO.getCheckoutItems(customerId);
        if (!items.isEmpty()) {
            return items;
        }
        return getSessionCheckoutItems(session);
    }

    public CheckoutResponse checkout(CheckoutRequest request, HttpSession session) {
        if (request == null) {
            return new CheckoutResponse(false, "Dữ liệu đặt hàng không hợp lệ.", null, null, null, null);
        }

        long customerId = request.getCustomerId();

        if (!isValidAddress(customerId, request.getMaDC())) {
            return new CheckoutResponse(false,
                    "Địa chỉ giao hàng không hợp lệ. Vui lòng chọn địa chỉ đã lưu hoặc thêm địa chỉ mới.",
                    null, null, null, null);
        }

        String maPT = request.getMaPT();

        if (maPT == null || maPT.trim().isEmpty()) {
            return new CheckoutResponse(false, "Vui lòng chọn phương thức thanh toán.", null, null, null, null);
        }

        maPT = maPT.trim().toUpperCase();

        if (!checkoutDAO.isValidPaymentMethod(maPT)) {
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
                    request.getGhiChu());

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

    private boolean isValidAddress(long customerId, Long maDC) {
        if (maDC == null) {
            return false;
        }
        DiaChi diaChi = addressRepository.findByMaDC(maDC);
        return diaChi != null
                && diaChi.getUser() != null
                && diaChi.getUser().getMaTK() == customerId;
    }
}
