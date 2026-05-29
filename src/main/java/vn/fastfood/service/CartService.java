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
    private final KhuyenMaiService khuyenMaiService;

    public CartService(MonAnRepository monAnRepository, KhuyenMaiService khuyenMaiService) {
        this.monAnRepository = monAnRepository;
        this.khuyenMaiService = khuyenMaiService;
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

    public CartAddResult addSessionCart(Long maMon, HttpSession session) {
        return addSessionCart(maMon, 1, session);
    }

    public CartAddResult addSessionCart(Long maMon, int soLuong, HttpSession session) {
        MonAn monAn = this.monAnRepository.findProduct(maMon);
        if (monAn == null) {
            return new CartAddResult(false, "Sản phẩm không tồn tại", 0);
        }

        khuyenMaiService.applyKhuyenMai(List.of(monAn));

        if (soLuong < 1) {
            soLuong = 1;
        }

        List<CartItem> cart = getSessionCart(session);
        CartItem cartItem = findCartItem(cart, monAn.getMaMon());
        double donGia = monAn.getGiaGiam();
        double donGiaGoc = monAn.getGia();

        if (cartItem != null) {
            int tongSoLuong = cartItem.getSoLuong() + soLuong;
            cartItem.setSoLuong(tongSoLuong);
            cartItem.setDonGia(donGia);
            cartItem.setDonGiaGoc(donGiaGoc);
            cartItem.setThanhTien(donGia * tongSoLuong);
        } else {
            CartItem item = new CartItem();
            item.setMaMon(monAn.getMaMon());
            item.setTenMon(monAn.getTenMon());
            item.setSoLuong(soLuong);
            item.setDonGia(donGia);
            item.setDonGiaGoc(donGiaGoc);
            item.setThanhTien(donGia * soLuong);
            item.setImageUrl(monAn.getImg());
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
