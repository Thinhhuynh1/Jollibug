package vn.fastfood.controller.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.entity.DanhMuc;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.ProductService;

@Controller
public class ManagerProductController {
    private final MonAnRepository monAnRepository;
    private final DanhMucRepository danhMucRepository;
    private final ProductService productService;

    ManagerProductController(MonAnRepository monAnRepository,
            DanhMucRepository danhMucRepository,
            ProductService productService) {
        this.monAnRepository = monAnRepository;
        this.danhMucRepository = danhMucRepository;
        this.productService = productService;
    }

    @GetMapping("/manager/products")
    public String getProductsPage(Model model,
            @RequestParam(value = "categoryID", required = false) Long categoryID,
            @RequestParam(value = "filter", required = false, defaultValue = "popular") String filter,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null) {
            keyword = keyword.trim();
        }

        List<MonAn> listMonAn;
        if ("price-low".equals(filter)) {
            listMonAn = this.monAnRepository.findMonAnPriceLow(categoryID, keyword);
        } else if ("price-high".equals(filter)) {
            listMonAn = this.monAnRepository.findMonAnPriceHigh(categoryID, keyword);
        } else if ("rating".equals(filter)) {
            listMonAn = this.monAnRepository.findMonAnBestSeller(categoryID, keyword);
        } else {
            listMonAn = this.monAnRepository.findMonAn(categoryID, keyword);
        }

        if ("active".equals(status)) {
            listMonAn = listMonAn.stream().filter(monAn -> monAn.isAvailable() && monAn.getSoLuongTon() > 0)
                    .collect(Collectors.toList());
        } else if ("inactive".equals(status)) {
            listMonAn = listMonAn.stream().filter(monAn -> !monAn.isAvailable()).collect(Collectors.toList());
        } else if ("out_of_stock".equals(status)) {
            listMonAn = listMonAn.stream().filter(monAn -> monAn.getSoLuongTon() == 0).collect(Collectors.toList());
        }

        model.addAttribute("listMonAn", listMonAn);
        model.addAttribute("danhMuc", this.danhMucRepository.findListDanhMuc());
        model.addAttribute("selectCategoryID", categoryID);
        model.addAttribute("selectedFilter", filter);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);

        return "manager/products/show";
    }

    @GetMapping("/manager/products/create")
    public String getProductsAddPage(Model model) {
        model.addAttribute("listDanhMuc", this.danhMucRepository.findListDanhMuc());
        return "manager/products/create";
    }

    @PostMapping("/manager/products/create")
    public String postProductsCreate(
            @RequestParam("tenMon") String tenMon,
            @RequestParam("maDM") Long maDM,
            @RequestParam("gia") long gia,
            @RequestParam("soLuongTon") long soLuongTon,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "productFile", required = false) MultipartFile productFile,
            @RequestParam("available") boolean available) {
        DanhMuc danhMuc = this.danhMucRepository.findById(maDM).orElse(null);
        if (danhMuc == null) {
            return "redirect:/manager/products";
        }

        MonAn monAn = new MonAn();
        monAn.setTenMon(tenMon);
        monAn.setDanhMuc(danhMuc);
        monAn.setGia(gia);
        monAn.setSoLuongTon(soLuongTon);
        monAn.setSoLuongDaBan(0);
        monAn.setMoTa(moTa);
        monAn.setImg(this.productService.storeProductImage(productFile, null));
        monAn.setAvailable(available);
        this.monAnRepository.save(monAn);

        return "redirect:/manager/products";
    }

    @GetMapping("/manager/products/detail")
    public String getProductsDetailPage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return "redirect:/manager/products";
        }
        model.addAttribute("monAn", monAn);
        return "manager/products/detail";
    }

    @GetMapping("/manager/products/update")
    public String getProductsUpdatePage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return "redirect:/manager/products";
        }
        model.addAttribute("monAn", monAn);
        model.addAttribute("listDanhMuc", this.danhMucRepository.findListDanhMuc());
        return "manager/products/update";
    }

    @PostMapping("/manager/products/update")
    public String postProductsUpdate(
            @RequestParam("productID") Long productID,
            @RequestParam("tenMon") String tenMon,
            @RequestParam("maDM") Long maDM,
            @RequestParam("gia") long gia,
            @RequestParam("soLuongTon") long soLuongTon,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "productFile", required = false) MultipartFile productFile,
            @RequestParam("available") boolean available) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        DanhMuc danhMuc = this.danhMucRepository.findById(maDM).orElse(null);
        if (monAn == null || danhMuc == null) {
            return "redirect:/manager/products";
        }

        monAn.setTenMon(tenMon);
        monAn.setDanhMuc(danhMuc);
        monAn.setGia(gia);
        monAn.setSoLuongTon(soLuongTon);
        monAn.setMoTa(moTa);
        monAn.setImg(this.productService.storeProductImage(productFile, monAn.getImg()));
        monAn.setAvailable(available);
        this.monAnRepository.save(monAn);

        return "redirect:/manager/products";
    }

    @GetMapping("/manager/products/delete")
    public String getProductsDeletePage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn == null) {
            return "redirect:/manager/products";
        }
        model.addAttribute("monAn", monAn);
        return "manager/products/delete";
    }

    @PostMapping("/manager/products/delete")
    public String postProductsDelete(@RequestParam("productID") Long productID) {
        MonAn monAn = this.monAnRepository.findProduct(productID);
        if (monAn != null) {
            this.monAnRepository.delete(monAn);
        }
        return "redirect:/manager/products";
    }

}
