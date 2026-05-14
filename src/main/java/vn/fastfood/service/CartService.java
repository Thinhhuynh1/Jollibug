package vn.fastfood.service;

import vn.fastfood.dao.CartDAO;
import vn.fastfood.model.CartItem;

import java.util.List;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();

    public List<CartItem> getCartItems(long customerId) {
        return cartDAO.getCartItemsByCustomerId(customerId);
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
}