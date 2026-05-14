package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.PromotionService;

@Controller
public class MenuController {
    private final MonAnRepository monAnRepository;
    private final DanhMucRepository danhMucRepository;
    private final PromotionService promotionService;

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
        model.addAttribute("monAn", this.monAnRepository.findProduct(productID));
        return "client/product";
    }
}
