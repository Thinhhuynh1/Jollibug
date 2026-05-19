package vn.fastfood.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public CartService(MonAnRepository monAnRepository) {
        this.monAnRepository = monAnRepository;
    }

    public List<CartItem> getCartItems(long customerId) {
        return cartDAO.getCartItemsByCustomerId(customerId);
    }

    public CartAddResult addSessionCart(Long productID, HttpSession session) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return new CartAddResult(false, "San pham khong ton tai.", 0);
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        CartItem cartItem = findCartItem(cart, monAn.getMaMon());
        BigDecimal gia = BigDecimal.valueOf(monAn.getGia());

        if (cartItem != null) {
            int soLuong = cartItem.getSoLuong() + 1;
            cartItem.setSoLuong(soLuong);
            cartItem.setDonGia(gia);
            cartItem.setThanhTien(gia.multiply(BigDecimal.valueOf(soLuong)));
        } else {
            CartItem item = new CartItem();
            item.setMaMon(monAn.getMaMon());
            item.setTenMon(monAn.getTenMon());
            item.setSoLuong(1);
            item.setDonGia(gia);
            item.setThanhTien(gia);
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
