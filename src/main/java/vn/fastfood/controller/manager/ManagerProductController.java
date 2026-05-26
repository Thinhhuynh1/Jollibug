package vn.fastfood.controller.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.fastfood.dto.ProductRequest;
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

        List<vn.fastfood.entity.MonAn> listMonAn;
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
            @RequestParam("available") boolean available,
            RedirectAttributes redirectAttributes) {
        try {
            productService.createProduct(buildRequest(tenMon, maDM, gia, soLuongTon, moTa, available), productFile);
            redirectAttributes.addFlashAttribute("message", "Đã thêm món ăn thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm món ăn.");
        }
        return "redirect:/manager/products";
    }

    @GetMapping("/manager/products/detail")
    public String getProductsDetailPage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        return productService.findProduct(productID)
                .map(monAn -> {
                    model.addAttribute("monAn", monAn);
                    return "manager/products/detail";
                })
                .orElse("redirect:/manager/products");
    }

    @GetMapping("/manager/products/update")
    public String getProductsUpdatePage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        return productService.findProduct(productID)
                .map(monAn -> {
                    model.addAttribute("monAn", monAn);
                    model.addAttribute("listDanhMuc", this.danhMucRepository.findListDanhMuc());
                    return "manager/products/update";
                })
                .orElse("redirect:/manager/products");
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
            @RequestParam("available") boolean available,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(productID, buildRequest(tenMon, maDM, gia, soLuongTon, moTa, available),
                    productFile);
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật món ăn thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật món ăn.");
        }
        return "redirect:/manager/products";
    }

    @GetMapping("/manager/products/delete")
    public String getProductsDeletePage(@RequestParam(value = "productID", required = false) Long productID,
            Model model) {
        return productService.findProduct(productID)
                .map(monAn -> {
                    model.addAttribute("monAn", monAn);
                    return "manager/products/delete";
                })
                .orElse("redirect:/manager/products");
    }

    @PostMapping("/manager/products/delete")
    public String postProductsDelete(@RequestParam("productID") Long productID,
            RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(productID);
            redirectAttributes.addFlashAttribute("message", "Đã xóa món ăn thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa món ăn. Món có thể đang được dùng trong đơn hàng.");
        }
        return "redirect:/manager/products";
    }

    private ProductRequest buildRequest(String tenMon, Long maDM, long gia, long soLuongTon, String moTa,
            boolean available) {
        ProductRequest request = new ProductRequest();
        request.setTenMon(tenMon);
        request.setMaDM(maDM);
        request.setGia(gia);
        request.setSoLuongTon(soLuongTon);
        request.setMoTa(moTa);
        request.setAvailable(available);
        return request;
    }
}
