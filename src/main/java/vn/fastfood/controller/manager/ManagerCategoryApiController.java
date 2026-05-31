package vn.fastfood.controller.manager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.fastfood.dto.CategoryRequest;
import vn.fastfood.dto.CategoryResponse;
import vn.fastfood.entity.DanhMuc;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.service.CategoryService;

@RestController
@RequestMapping("/api/manager/categories")
public class ManagerCategoryApiController {
    private final CategoryService categoryService;
    private final DanhMucRepository danhMucRepository;

    public ManagerCategoryApiController(CategoryService categoryService, DanhMucRepository danhMucRepository) {
        this.categoryService = categoryService;
        this.danhMucRepository = danhMucRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        List<CategoryResponse> data = categoryService.listCategories();
        return ResponseEntity.ok(Map.of("success", true, "total", data.size(), "data", data));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable long categoryId) {
        return categoryService.findCategory(categoryId)
                .map(d -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", CategoryResponse.from(d, danhMucRepository.countMonAn(d.getMaDM())))))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Khong tim thay danh muc.")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CategoryRequest request) {
        return buildResponse(() -> categoryService.createCategory(request), "Da them danh muc thanh cong.");
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable long categoryId,
            @RequestBody CategoryRequest request) {
        return buildResponse(() -> categoryService.updateCategory(categoryId, request),
                "Da cap nhat danh muc thanh cong.");
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Da xoa danh muc thanh cong."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> buildResponse(CategorySupplier supplier, String message) {
        try {
            DanhMuc danhMuc = supplier.get();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message,
                    "data", CategoryResponse.from(danhMuc, danhMucRepository.countMonAn(danhMuc.getMaDM()))));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @FunctionalInterface
    private interface CategorySupplier {
        DanhMuc get();
    }
}
