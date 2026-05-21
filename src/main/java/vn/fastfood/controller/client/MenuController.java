package vn.fastfood.controller.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.fastfood.dao.ReviewDAO;
import vn.fastfood.entity.MonAn;
import vn.fastfood.model.CartItem;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.PromotionService;

import java.net.URL;
import java.net.MalformedURLException;

@Controller
public class MenuController {
    private final MonAnRepository monAnRepository;
    private final DanhMucRepository danhMucRepository;
    private final PromotionService promotionService;
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public MenuController(MonAnRepository monAnRepository,
            DanhMucRepository danhMucRepository,
            PromotionService promotionService) {
        this.monAnRepository = monAnRepository;
        this.danhMucRepository = danhMucRepository;
        this.promotionService = promotionService;
    }

    @GetMapping("/menu")
    public String getMenuPage(Model model,
            @RequestParam(value = "categoryID", required = false) Long categoryID,
            @RequestParam(value = "filter", required = false, defaultValue = "popular") String filter,
            @RequestParam(value = "keyword", required = false) String keyword) {

        List<MonAn> list;
        if ("price-low".equals(filter)) {
            list = this.monAnRepository.findMonAnPriceLow(categoryID, keyword);
        } else if ("price-high".equals(filter)) {
            list = this.monAnRepository.findMonAnPriceHigh(categoryID, keyword);
        } else if ("rating".equals(filter)) {
            list = this.monAnRepository.findMonAnBestSeller(categoryID, keyword);
        } else {
            list = this.monAnRepository.findMonAn(categoryID, keyword);
        }

        this.promotionService.applyPromotions(list);

        model.addAttribute("listMonAn", list);
        model.addAttribute("selectCategoryID", categoryID);
        model.addAttribute("selectedFilter", filter);
        model.addAttribute("danhMuc", this.danhMucRepository.findAll());
        return "client/menu";
    }

    @GetMapping("/product")
    public String getProductDetail(Model model,
            @RequestParam("productID") Long productID) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        model.addAttribute("monAn", monAn);
        model.addAttribute("productReviews", this.reviewDAO.findReviewsByProduct(productID));
        model.addAttribute("averageRating", this.reviewDAO.getAverageRatingByProduct(productID));
        model.addAttribute("reviewCount", this.reviewDAO.countReviewsByProduct(productID));
        return "client/product";
    }

    @PostMapping("/addCart")
    public String addToCart(@RequestParam("productID") Long productID,
            HttpServletRequest request,
            HttpSession session) {

        int quantity = 1;
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            session.setAttribute("cartError", "Sản phẩm không tồn tại");
            return "redirect:/menu";
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        CartItem cartItem = null;
        for (CartItem item : cart) {
            if (item.getMaMon() == monAn.getMaMon()) {
                cartItem = item;
                break;
            }
        }

        BigDecimal gia = BigDecimal.valueOf(monAn.getGia());
        if (cartItem != null) {
            int soLuong = cartItem.getSoLuong() + quantity;
            cartItem.setSoLuong(soLuong);
            cartItem.setDonGia(gia);
            cartItem.setThanhTien(gia.multiply(BigDecimal.valueOf(soLuong)));
        } else {
            CartItem item = new CartItem();
            item.setMaMon(monAn.getMaMon());
            item.setTenMon(monAn.getTenMon());
            item.setSoLuong(quantity);
            item.setDonGia(gia);
            item.setThanhTien(gia.multiply(BigDecimal.valueOf(quantity)));
            item.setImageUrl(monAn.getImg());
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                URL url = new URL(referer);
                String path = url.getPath();
                String query = url.getQuery();
                String redirectUrl = path + (query != null ? "?" + query : "");
                return "redirect:" + redirectUrl;
            } catch (MalformedURLException e) {
                // fallback nếu referer không hợp lệ
            }
        }
        return "redirect:/menu";
    }

}
