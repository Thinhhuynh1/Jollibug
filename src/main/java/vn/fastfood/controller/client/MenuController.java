package vn.fastfood.controller.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
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
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.CartService;
import vn.fastfood.service.KhuyenMaiService;

@Controller
public class MenuController {
    private final MonAnRepository monAnRepository;
    private final DanhMucRepository danhMucRepository;
    private final KhuyenMaiService khuyenmaiService;
    private final CartService cartService;
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public MenuController(MonAnRepository monAnRepository,
            DanhMucRepository danhMucRepository,
            KhuyenMaiService khuyenmaiService,
            CartService cartService) {
        this.monAnRepository = monAnRepository;
        this.danhMucRepository = danhMucRepository;
        this.khuyenmaiService = khuyenmaiService;
        this.cartService = cartService;
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

        this.khuyenmaiService.applyKhuyenMai(list);
        sortByDiscountedPrice(list, filter);

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
        if (monAn != null) {
            this.khuyenmaiService.applyKhuyenMai(java.util.List.of(monAn));
        }
        model.addAttribute("monAn", monAn);
        return "client/product";
    }

    @PostMapping("/addCart")
    public String addToCart(@RequestParam("productID") Long productID,
            HttpServletRequest request,
            HttpSession session) {

        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            session.setAttribute("cartError", "Sản phẩm không tồn tại");
            return "redirect:/menu";
        }

        this.khuyenmaiService.applyKhuyenMai(java.util.List.of(monAn));
        cartService.addSessionCart(monAn, 1, session);

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                URL url = new URL(referer);
                String path = url.getPath();
                String query = url.getQuery();
                String redirectUrl = path + (query != null ? "?" + query : "");
                return "redirect:" + redirectUrl;
            } catch (MalformedURLException e) {
                // Chuyển về thực đơn nếu referer không hợp lệ
            }
        }
        return "redirect:/menu";
    }

    private void sortByDiscountedPrice(List<MonAn> products, String filter) {
        if (products == null || products.isEmpty()) {
            return;
        }

        if ("price-low".equals(filter)) {
            products.sort(Comparator.comparingLong(MonAn::getGiaGiam)
                    .thenComparingLong(MonAn::getGia)
                    .thenComparingLong(MonAn::getMaMon));
            return;
        }

        if ("price-high".equals(filter)) {
            products.sort(Comparator.comparingLong(MonAn::getGiaGiam)
                    .reversed()
                    .thenComparing(Comparator.comparingLong(MonAn::getGia).reversed())
                    .thenComparingLong(MonAn::getMaMon));
        }
    }
}
