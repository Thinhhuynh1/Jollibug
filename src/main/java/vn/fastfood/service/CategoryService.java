package vn.fastfood.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.fastfood.dto.CategoryRequest;
import vn.fastfood.dto.CategoryResponse;
import vn.fastfood.entity.DanhMuc;
import vn.fastfood.repository.DanhMucRepository;

@Service
public class CategoryService {
    private final DanhMucRepository danhMucRepository;

    public CategoryService(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    public List<CategoryResponse> listCategories() {
        return danhMucRepository.findListDanhMuc().stream()
                .map(d -> CategoryResponse.from(d, danhMucRepository.countMonAn(d.getMaDM())))
                .collect(Collectors.toList());
    }

    public Optional<DanhMuc> findCategory(Long categoryId) {
        if (categoryId == null) {
            return Optional.empty();
        }
        DanhMuc danhMuc = danhMucRepository.findDanhMuc(categoryId);
        return danhMuc == null ? Optional.empty() : Optional.of(danhMuc);
    }

    public DanhMuc createCategory(CategoryRequest request) {
        validateRequest(request);
        DanhMuc danhMuc = new DanhMuc();
        applyRequest(danhMuc, request);
        return danhMucRepository.save(danhMuc);
    }

    public DanhMuc updateCategory(long categoryId, CategoryRequest request) {
        validateRequest(request);
        DanhMuc danhMuc = findCategoryOrThrow(categoryId);
        applyRequest(danhMuc, request);
        return danhMucRepository.save(danhMuc);
    }

    public void deleteCategory(long categoryId) {
        DanhMuc danhMuc = findCategoryOrThrow(categoryId);
        long soMon = danhMucRepository.countMonAn(categoryId);
        if (soMon > 0) {
            throw new IllegalArgumentException("Khong the xoa danh muc dang co " + soMon + " mon an.");
        }
        danhMucRepository.delete(danhMuc);
    }

    private DanhMuc findCategoryOrThrow(long categoryId) {
        return findCategory(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay danh muc."));
    }

    private void validateRequest(CategoryRequest request) {
        if (request == null || request.getTenDM() == null || request.getTenDM().isBlank()) {
            throw new IllegalArgumentException("Ten danh muc khong duoc de trong.");
        }
    }

    private void applyRequest(DanhMuc danhMuc, CategoryRequest request) {
        danhMuc.setTenDM(request.getTenDM().trim());
        danhMuc.setMoTa(request.getMoTa());
        danhMuc.setAvailable(request.getAvailable() == null || request.getAvailable());
    }
}
