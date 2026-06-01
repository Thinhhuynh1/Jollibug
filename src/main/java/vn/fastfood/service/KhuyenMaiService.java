package vn.fastfood.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.fastfood.entity.ChiTietKhuyenMai;
import vn.fastfood.entity.ChuongTrinhKhuyenMai;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.ChuongTrinhKhuyenMaiRepository;
import vn.fastfood.repository.MonAnRepository;

@Service
public class KhuyenMaiService {
    private static final String APPLY_ALL = "ALL";
    private static final String APPLY_ITEM = "ITEM";

    private final ChuongTrinhKhuyenMaiRepository khuyenMaiRepository;
    private final MonAnRepository monAnRepository;

    public KhuyenMaiService(ChuongTrinhKhuyenMaiRepository khuyenMaiRepository,
            MonAnRepository monAnRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.monAnRepository = monAnRepository;
    }

    public List<ChuongTrinhKhuyenMai> findKhuyenMai(String keyword) {
        if (keyword == null) {
            return khuyenMaiRepository.searchByName(null);
        }

        String normalizedKeyword = keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            normalizedKeyword = null;
        }

        return khuyenMaiRepository.searchByName(normalizedKeyword);
    }

    public ChuongTrinhKhuyenMai findKhuyenMaiById(Long maKM) {
        if (maKM == null) {
            return null;
        }
        return khuyenMaiRepository.findById(maKM).orElse(null);
    }

    public List<MonAn> findMonAnApDung(ChuongTrinhKhuyenMai khuyenMai) {
        if (khuyenMai == null || !APPLY_ITEM.equals(khuyenMai.getPhamViApDung())) {
            return List.of();
        }

        List<Long> monAnIds = khuyenMai.getMonAnDuocApDungIds();
        if (monAnIds.isEmpty()) {
            return List.of();
        }

        return monAnRepository.findAllById(monAnIds);
    }

    @Transactional
    public ChuongTrinhKhuyenMai createKhuyenMai(String tenKM,
            Double phanTramGiam,
            String phamViApDung,
            List<Long> selectedMonAnIds,
            String startDate,
            String endDate) {
        ChuongTrinhKhuyenMai khuyenMai = new ChuongTrinhKhuyenMai();
        if (tenKM == null || tenKM.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");
        }
        if (phanTramGiam == null || phanTramGiam <= 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException("Phần trăm giảm phải lớn hơn 0 và không vượt quá 100.");
        }

        String phamVi = APPLY_ITEM.equals(phamViApDung) ? APPLY_ITEM : APPLY_ALL;
        if (APPLY_ITEM.equals(phamVi) && (selectedMonAnIds == null || selectedMonAnIds.isEmpty())) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một món áp dụng.");
        }

        LocalDateTime ngayBatDau = startDate == null || startDate.isBlank()
                ? null
                : LocalDate.parse(startDate).atTime(LocalTime.MIN);
        LocalDateTime ngayKetThuc = endDate == null || endDate.isBlank()
                ? null
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);

        khuyenMai.setTenKM(tenKM.trim());
        khuyenMai.setPhanTramGiam(phanTramGiam);
        khuyenMai.setPhamViApDung(phamVi);
        khuyenMai.setNgayBatDau(ngayBatDau);
        khuyenMai.setNgayKetThuc(ngayKetThuc);
        updateChiTietKhuyenMai(khuyenMai, phamVi, selectedMonAnIds);
        return khuyenMaiRepository.save(khuyenMai);
    }

    @Transactional
    public ChuongTrinhKhuyenMai updateKhuyenMai(Long maKM,
            String tenKM,
            Double phanTramGiam,
            String phamViApDung,
            List<Long> selectedMonAnIds,
            String startDate,
            String endDate) {
        ChuongTrinhKhuyenMai khuyenMai = findKhuyenMaiById(maKM);
        if (khuyenMai == null) {
            return null;
        }

        if (tenKM == null || tenKM.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");
        }
        if (phanTramGiam == null || phanTramGiam <= 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException("Phần trăm giảm phải lớn hơn 0 và không vượt quá 100.");
        }

        String phamVi = APPLY_ITEM.equals(phamViApDung) ? APPLY_ITEM : APPLY_ALL;
        if (APPLY_ITEM.equals(phamVi) && (selectedMonAnIds == null || selectedMonAnIds.isEmpty())) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một món áp dụng.");
        }

        LocalDateTime ngayBatDau = startDate == null || startDate.isBlank()
                ? null
                : LocalDate.parse(startDate).atTime(LocalTime.MIN);
        LocalDateTime ngayKetThuc = endDate == null || endDate.isBlank()
                ? null
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);

        khuyenMai.setTenKM(tenKM.trim());
        khuyenMai.setPhanTramGiam(phanTramGiam);
        khuyenMai.setPhamViApDung(phamVi);
        khuyenMai.setNgayBatDau(ngayBatDau);
        khuyenMai.setNgayKetThuc(ngayKetThuc);
        updateChiTietKhuyenMai(khuyenMai, phamVi, selectedMonAnIds);
        return khuyenMaiRepository.save(khuyenMai);
    }

    @Transactional
    public void deleteKhuyenMai(Long maKM) {
        if (maKM != null) {
            khuyenMaiRepository.deleteById(maKM);
        }
    }

    public void applyKhuyenMai(List<MonAn> monAnList) {
        if (monAnList == null || monAnList.isEmpty()) {
            return;
        }

        resetKhuyenMai(monAnList);

        List<ChuongTrinhKhuyenMai> khuyenMaiDangHoatDong =
                khuyenMaiRepository.findActivePromotions(LocalDateTime.now());
        if (khuyenMaiDangHoatDong.isEmpty()) {
            return;
        }

        for (MonAn monAn : monAnList) {
            double phanTramGiamMax = 0;

            for (ChuongTrinhKhuyenMai khuyenMai : khuyenMaiDangHoatDong) {
                double phanTramGiam = getDiscountPercent(monAn, khuyenMai);
                if (phanTramGiam > phanTramGiamMax) {
                    phanTramGiamMax = phanTramGiam;
                }
            }

            if (phanTramGiamMax <= 0) {
                continue;
            }

            monAn.setHasGiamGia(true);
            monAn.setPhanTramGiam(phanTramGiamMax);
            monAn.setGiaGiam(Math.round(monAn.getGia() * (1 - phanTramGiamMax / 100)));
        }
    }

    private void updateChiTietKhuyenMai(ChuongTrinhKhuyenMai khuyenMai,
            String phamViApDung,
            List<Long> selectedMonAnIds) {
        if (!APPLY_ITEM.equals(phamViApDung) || selectedMonAnIds == null || selectedMonAnIds.isEmpty()) {
            if (khuyenMai.getChiTietKhuyenMais() != null) {
                khuyenMai.getChiTietKhuyenMais().clear();
            } else {
                khuyenMai.setChiTietKhuyenMais(new ArrayList<>());
            }
            return;
        }

        List<MonAn> monAnDuocChon = monAnRepository.findAllById(selectedMonAnIds);
        List<ChiTietKhuyenMai> chiTietKhuyenMaiList = new ArrayList<>();

        for (MonAn monAn : monAnDuocChon) {
            ChiTietKhuyenMai chiTiet = new ChiTietKhuyenMai();
            chiTiet.setKhuyenMai(khuyenMai);
            chiTiet.setMonAn(monAn);
            chiTiet.setPhanTramGiam(khuyenMai.getPhanTramGiam());
            chiTietKhuyenMaiList.add(chiTiet);
        }

        if (khuyenMai.getChiTietKhuyenMais() != null) {
            khuyenMai.getChiTietKhuyenMais().clear();
            khuyenMai.getChiTietKhuyenMais().addAll(chiTietKhuyenMaiList);
        } else {
            khuyenMai.setChiTietKhuyenMais(chiTietKhuyenMaiList);
        }
    }

    private void resetKhuyenMai(List<MonAn> monAnList) {
        for (MonAn monAn : monAnList) {
            monAn.setHasGiamGia(false);
            monAn.setPhanTramGiam(0);
            monAn.setGiaGiam(monAn.getGia());
        }
    }

    private double getDiscountPercent(MonAn monAn, ChuongTrinhKhuyenMai khuyenMai) {
        if (khuyenMai == null) {
            return 0;
        }

        String phamViApDung = khuyenMai.getPhamViApDung();
        if (APPLY_ALL.equals(phamViApDung)) {
            return getDefaultDiscountPercent(khuyenMai);
        }
        if (APPLY_ITEM.equals(phamViApDung)) {
            return getItemDiscountPercent(monAn, khuyenMai);
        }
        return 0;
    }

    private double getItemDiscountPercent(MonAn monAn, ChuongTrinhKhuyenMai khuyenMai) {
        List<ChiTietKhuyenMai> chiTietKhuyenMaiList = khuyenMai.getChiTietKhuyenMais();
        if (chiTietKhuyenMaiList == null || chiTietKhuyenMaiList.isEmpty()) {
            return 0;
        }

        for (ChiTietKhuyenMai chiTiet : chiTietKhuyenMaiList) {
            if (chiTiet.getMonAn() == null) {
                continue;
            }
            if (!Objects.equals(chiTiet.getMonAn().getMaMon(), monAn.getMaMon())) {
                continue;
            }
            return chiTiet.getPhanTramGiam() != null
                    ? chiTiet.getPhanTramGiam()
                    : getDefaultDiscountPercent(khuyenMai);
        }

        return 0;
    }

    private double getDefaultDiscountPercent(ChuongTrinhKhuyenMai khuyenMai) {
        return khuyenMai.getPhanTramGiam() == null ? 0 : khuyenMai.getPhanTramGiam();
    }

}
