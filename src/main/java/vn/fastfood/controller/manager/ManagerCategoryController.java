package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.fastfood.dto.CategoryRequest;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.service.CategoryService;

@Controller
public class ManagerCategoryController {
    private final CategoryService categoryService;
    private final DanhMucRepository danhMucRepository;

    ManagerCategoryController(CategoryService categoryService, DanhMucRepository danhMucRepository) {
        this.categoryService = categoryService;
        this.danhMucRepository = danhMucRepository;
    }

    @GetMapping("/manager/categories")
    public String getCategoriesPage(Model model) {
        model.addAttribute("listDanhMuc", categoryService.listCategories());
        return "manager/categories/show";
    }

    @GetMapping("/manager/categories/create")
    public String getCategoriesAddPage() {
        return "manager/categories/create";
    }

    @PostMapping("/manager/categories/create")
    public String postCreate(@RequestParam("tenDM") String tenDM,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "available", defaultValue = "true") boolean available,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(buildRequest(tenDM, moTa, available));
            redirectAttributes.addFlashAttribute("message", "Đã thêm danh mục thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/categories";
    }

    @GetMapping("/manager/categories/detail")
    public String getCategoriesDetailPage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        return categoryService.findCategory(categoryID)
                .map(danhMuc -> {
                    model.addAttribute("danhMuc", danhMuc);
                    model.addAttribute("soLuongMon", danhMucRepository.countMonAn(danhMuc.getMaDM()));
                    return "manager/categories/detail";
                })
                .orElse("redirect:/manager/categories");
    }

    @GetMapping("/manager/categories/update")
    public String getCategoriesUpdatePage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        return categoryService.findCategory(categoryID)
                .map(danhMuc -> {
                    model.addAttribute("danhMuc", danhMuc);
                    return "manager/categories/update";
                })
                .orElse("redirect:/manager/categories");
    }

    @PostMapping("/manager/categories/update")
    public String postCategoriesUpdate(
            @RequestParam("categoryID") Long categoryID,
            @RequestParam("tenDM") String tenDM,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "available", defaultValue = "true") boolean available,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(categoryID, buildRequest(tenDM, moTa, available));
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật danh mục thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/categories";
    }

    @GetMapping("/manager/categories/delete")
    public String getCategoriesDeletePage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        return categoryService.findCategory(categoryID)
                .map(danhMuc -> {
                    model.addAttribute("danhMuc", danhMuc);
                    model.addAttribute("soLuongMon", danhMucRepository.countMonAn(danhMuc.getMaDM()));
                    return "manager/categories/delete";
                })
                .orElse("redirect:/manager/categories");
    }

    @PostMapping("/manager/categories/delete")
    public String postDelete(@RequestParam("categoryID") Long categoryID,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(categoryID);
            redirectAttributes.addFlashAttribute("message", "Đã xóa danh mục thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/categories";
    }

    private CategoryRequest buildRequest(String tenDM, String moTa, boolean available) {
        CategoryRequest request = new CategoryRequest();
        request.setTenDM(tenDM);
        request.setMoTa(moTa);
        request.setAvailable(available);
        return request;
    }
}
