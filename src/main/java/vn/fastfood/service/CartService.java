package vn.fastfood.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import vn.fastfood.dao.CartDAO;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.CartItem;
import vn.fastfood.repository.MonAnRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {
    private final CartDAO cartDAO = new CartDAO();
    private final MonAnRepository monAnRepository;
    private final PromotionService promotionService;

    public CartService(MonAnRepository monAnRepository, PromotionService promotionService) {
        this.monAnRepository = monAnRepository;
        this.promotionService = promotionService;
    }

    public List<CartItem> getCartItems(long customerId) {
        return cartDAO.getCartItemsByCustomerId(customerId);
    }

    public CartAddResult addSessionCart(Long productID, HttpSession session) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return new CartAddResult(false, "San pham khong ton tai.", 0);
        }

        // Áp dụng chương trình khuyến mãi để tính giá giảm
        promotionService.applyPromotions(Collections.singletonList(monAn));

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        CartItem cartItem = findCartItem(cart, monAn.getMaMon());
        BigDecimal giaGoc = BigDecimal.valueOf(monAn.getGia());
        BigDecimal giaGiam = BigDecimal.valueOf(monAn.getGiaGiam());

        if (cartItem != null) {
            int soLuong = cartItem.getSoLuong() + 1;
            cartItem.setSoLuong(soLuong);
            cartItem.setDonGia(giaGiam);
            cartItem.setDonGiaGoc(giaGoc);
            cartItem.setThanhTien(giaGiam.multiply(BigDecimal.valueOf(soLuong)));
        } else {
            CartItem item = new CartItem();
            item.setMaMon(monAn.getMaMon());
            item.setTenMon(monAn.getTenMon());
            item.setSoLuong(1);
            item.setDonGia(giaGiam);
            item.setDonGiaGoc(giaGoc);
            item.setThanhTien(giaGiam);
            item.setImageUrl(monAn.getImg());
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        return new CartAddResult(true, "Them vao gio hang thanh cong.", countCartItems(cart));
    }

    public boolean updateQuantity(long customerId, long maMon, int soLuong) {
        if (soLuong <= 0) {
            return cartDAO.removeCartItem(customerId, maMon);
        }

        return cartDAO.updateCartItemQuantity(customerId, maMon, soLuong);
    }

    public boolean removeItem(long customerId, long maMon) {
        return cartDAO.removeCartItem(customerId, maMon);
    }

    private CartItem findCartItem(List<CartItem> cart, long maMon) {
        for (CartItem item : cart) {
            if (item.getMaMon() == maMon) {
                return item;
            }
        }
        return null;
    }

    private int countCartItems(List<CartItem> cart) {
        int total = 0;
        for (CartItem item : cart) {
            total += item.getSoLuong();
        }
        return total;
    }

    public record CartAddResult(boolean success, String message, int cartCount) {
    }
}
