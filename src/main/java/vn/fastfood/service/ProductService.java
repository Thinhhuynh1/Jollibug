package vn.fastfood.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.fastfood.dto.ProductRequest;
import vn.fastfood.entity.DanhMuc;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.DanhMucRepository;
import vn.fastfood.repository.MonAnRepository;

@Service
public class ProductService {
    private final MonAnRepository monAnRepository;
    private final DanhMucRepository danhMucRepository;

    public ProductService(MonAnRepository monAnRepository, DanhMucRepository danhMucRepository) {
        this.monAnRepository = monAnRepository;
        this.danhMucRepository = danhMucRepository;
    }

    public MonAn createProduct(ProductRequest request, MultipartFile productFile) {
        validateRequest(request);
        DanhMuc danhMuc = findCategory(request.getMaDM());

        MonAn monAn = new MonAn();
        applyRequest(monAn, request, danhMuc);
        monAn.setSoLuongDaBan(0);
        monAn.setImg(storeProductImage(productFile, null));
        return monAnRepository.save(monAn);
    }

    public MonAn updateProduct(long productId, ProductRequest request, MultipartFile productFile) {
        validateRequest(request);
        MonAn monAn = findProductOrThrow(productId);
        DanhMuc danhMuc = findCategory(request.getMaDM());
        applyRequest(monAn, request, danhMuc);
        monAn.setImg(storeProductImage(productFile, monAn.getImg()));
        return monAnRepository.save(monAn);
    }

    public void deleteProduct(long productId) {
        MonAn monAn = findProductOrThrow(productId);
        monAnRepository.delete(monAn);
    }

    public Optional<MonAn> findProduct(long productId) {
        return Optional.ofNullable(monAnRepository.findProduct(productId));
    }

    public String storeProductImage(MultipartFile file, String currentImageName) {
        if (file == null || file.isEmpty()) {
            return currentImageName;
        }

        String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString().replace(" ", "_");
        Path imageDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "images");
        Path imagePath = imageDir.resolve(fileName);

        try {
            Files.createDirectories(imageDir);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Khong the luu hinh anh", e);
        }
    }

    private void validateRequest(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Du lieu mon an khong hop le.");
        }
        if (request.getTenMon() == null || request.getTenMon().isBlank()) {
            throw new IllegalArgumentException("Ten mon an khong duoc de trong.");
        }
        if (request.getMaDM() == null) {
            throw new IllegalArgumentException("Ma danh muc khong hop le.");
        }
        if (request.getGia() == null || request.getGia() < 0) {
            throw new IllegalArgumentException("Gia ban khong hop le.");
        }
        if (request.getSoLuongTon() == null || request.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("Ton kho khong hop le.");
        }
    }

    private DanhMuc findCategory(Long maDM) {
        return danhMucRepository.findById(maDM)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay danh muc."));
    }

    private MonAn findProductOrThrow(long productId) {
        MonAn monAn = monAnRepository.findProduct(productId);
        if (monAn == null) {
            throw new IllegalArgumentException("Khong tim thay mon an.");
        }
        return monAn;
    }

    private void applyRequest(MonAn monAn, ProductRequest request, DanhMuc danhMuc) {
        monAn.setTenMon(request.getTenMon().trim());
        monAn.setDanhMuc(danhMuc);
        monAn.setGia(request.getGia());
        monAn.setSoLuongTon(request.getSoLuongTon());
        monAn.setMoTa(request.getMoTa());
        monAn.setAvailable(request.getAvailable() == null || request.getAvailable());
    }
}
