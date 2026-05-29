package vn.fastfood.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.CartItem;
import vn.fastfood.repository.MonAnRepository;

@Service
public class CartService {
    private final MonAnRepository monAnRepository;

    public CartService(MonAnRepository monAnRepository) {
        this.monAnRepository = monAnRepository;
    }

    public record CartAddResult(boolean success, String message, int cartCount) {
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

    public List<CartItem> getSessionCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) cartObj;
            return cart;
        }

        List<CartItem> cart = new ArrayList<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    public CartAddResult addSessionCart(Long productID, HttpSession session) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return new CartAddResult(false, "Sản phẩm không tồn tại", 0);
        }

        return addSessionCart(monAn, 1, session);
    }

    public CartAddResult addSessionCart(Long productID, int quantity, HttpSession session) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return new CartAddResult(false, "Sản phẩm không tồn tại", 0);
        }

        return addSessionCart(monAn, quantity, session);
    }

    public CartAddResult addSessionCart(MonAn monAn, int quantity, HttpSession session) {
        if (monAn == null) {
            return new CartAddResult(false, "Sản phẩm không tồn tại", 0);
        }

        if (quantity < 1) {
            quantity = 1;
        }

        List<CartItem> cart = getSessionCart(session);
        CartItem cartItem = findCartItem(cart, monAn.getMaMon());
        double gia = monAn.getGiaGiam();
        double giaGoc = monAn.getGia();

        if (cartItem != null) {
            int soLuong = cartItem.getSoLuong() + quantity;
            cartItem.setSoLuong(soLuong);
            cartItem.setDonGia(gia);
            cartItem.setThanhTien(gia * soLuong);
            cartItem.setDonGiaGoc(giaGoc);
        } else {
            CartItem item = new CartItem();
            item.setMaMon(monAn.getMaMon());
            item.setTenMon(monAn.getTenMon());
            item.setSoLuong(quantity);
            item.setDonGia(gia);
            item.setThanhTien(gia * quantity);
            item.setImageUrl(monAn.getImg());
            item.setDonGiaGoc(giaGoc);
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        return new CartAddResult(true, "Thêm vào giỏ hàng thành công", countCartItems(cart));
    }

    public boolean updateSessionQuantity(HttpSession session, long maMon, int soLuong) {
        if (soLuong < 1) {
            return false;
        }

        List<CartItem> cart = getSessionCart(session);
        CartItem item = findCartItem(cart, maMon);
        if (item == null) {
            return false;
        }

        item.setSoLuong(soLuong);
        item.setThanhTien(item.getDonGia() * soLuong);
        session.setAttribute("cart", cart);
        return true;
    }

    public boolean removeSessionItem(HttpSession session, long maMon) {
        List<CartItem> cart = getSessionCart(session);
        Iterator<CartItem> iterator = cart.iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getMaMon() == maMon) {
                iterator.remove();
                session.setAttribute("cart", cart);
                return true;
            }
        }

        session.setAttribute("cart", cart);
        return false;
    }
}
