package vn.fastfood.controller.manager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.dto.ProductRequest;
import vn.fastfood.dto.ProductResponse;
import vn.fastfood.entity.MonAn;
import vn.fastfood.service.ProductService;

@RestController
@RequestMapping("/api/manager/products")
public class ManagerProductApiController {
    private final ProductService productService;

    public ManagerProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listProducts(
            @RequestParam(value = "categoryID", required = false) Long categoryID,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "filter", required = false, defaultValue = "popular") String filter,
            @RequestParam(value = "status", required = false) String status) {
        List<ProductResponse> products = productService.searchProducts(categoryID, keyword, filter, status).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", products.size(),
                "data", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable long productId) {
        return productService.findProduct(productId)
                .map(monAn -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", ProductResponse.from(monAn))))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Khong tim thay mon an.")));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductRequest request) {
        return buildResponse(() -> productService.createProduct(request, null),
                "Da them mon an thanh cong.");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createProductWithImage(
            @RequestParam("tenMon") String tenMon,
            @RequestParam("maDM") Long maDM,
            @RequestParam("gia") Long gia,
            @RequestParam("soLuongTon") Long soLuongTon,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "available", defaultValue = "true") boolean available,
            @RequestPart(value = "productFile", required = false) MultipartFile productFile) {
        ProductRequest request = buildRequest(tenMon, maDM, gia, soLuongTon, moTa, available);
        return buildResponse(() -> productService.createProduct(request, productFile),
                "Da them mon an thanh cong.");
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable long productId,
            @RequestBody ProductRequest request) {
        return buildResponse(() -> productService.updateProduct(productId, request, null),
                "Da cap nhat mon an thanh cong.");
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProductWithImage(
            @PathVariable long productId,
            @RequestParam("tenMon") String tenMon,
            @RequestParam("maDM") Long maDM,
            @RequestParam("gia") Long gia,
            @RequestParam("soLuongTon") Long soLuongTon,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "available", defaultValue = "true") boolean available,
            @RequestPart(value = "productFile", required = false) MultipartFile productFile) {
        ProductRequest request = buildRequest(tenMon, maDM, gia, soLuongTon, moTa, available);
        return buildResponse(() -> productService.updateProduct(productId, request, productFile),
                "Da cap nhat mon an thanh cong.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Da xoa mon an thanh cong."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Khong the xoa mon an. Mon co the dang duoc su dung trong don hang."));
        }
    }

    private ProductRequest buildRequest(String tenMon, Long maDM, Long gia, Long soLuongTon, String moTa,
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

    private ResponseEntity<Map<String, Object>> buildResponse(ProductSupplier supplier, String successMessage) {
        try {
            MonAn monAn = supplier.get();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", successMessage,
                    "data", ProductResponse.from(monAn)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Co loi xay ra khi xu ly mon an."));
        }
    }

    @FunctionalInterface
    private interface ProductSupplier {
        MonAn get();
    }
}
