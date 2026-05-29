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

    public KhuyenMaiService(ChuongTrinhKhuyenMaiRepository khuyenMaiRepository, MonAnRepository monAnRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.monAnRepository = monAnRepository;
    }

    public List<ChuongTrinhKhuyenMai> findKhuyenMai(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return khuyenMaiRepository.searchByName(null);
        }

        return khuyenMaiRepository.searchByName(keyword.trim());
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
    public ChuongTrinhKhuyenMai createKhuyenMai(String tenKM, Double phanTramGiam, String phamViApDung,
            List<Long> selectedMonAnIds, String startDate, String endDate) {
        validateTenKhuyenMai(tenKM);
        validatePhanTramGiam(phanTramGiam);

        String phamVi = APPLY_ALL;
        if (APPLY_ITEM.equalsIgnoreCase(phamViApDung)) {
            phamVi = APPLY_ITEM;
        }
        validateSelectedMonAn(phamVi, selectedMonAnIds);

        LocalDateTime ngayBatDau = null;
        if (startDate != null && !startDate.isBlank()) {
            ngayBatDau = LocalDate.parse(startDate).atTime(LocalTime.MIN);
        }

        LocalDateTime ngayKetThuc = null;
        if (endDate != null && !endDate.isBlank()) {
            ngayKetThuc = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        validateKhoangNgay(ngayBatDau, ngayKetThuc);

        ChuongTrinhKhuyenMai khuyenMai = new ChuongTrinhKhuyenMai();
        khuyenMai.setTenKM(tenKM.trim());
        khuyenMai.setPhanTramGiam(phanTramGiam);
        khuyenMai.setPhamViApDung(phamVi);
        khuyenMai.setNgayBatDau(ngayBatDau);
        khuyenMai.setNgayKetThuc(ngayKetThuc);
        updateChiTietKhuyenMai(khuyenMai, phamVi, selectedMonAnIds);
        return khuyenMaiRepository.save(khuyenMai);
    }

    @Transactional
    public ChuongTrinhKhuyenMai updateKhuyenMai(Long maKM, String tenKM, Double phanTramGiam,
            String phamViApDung, List<Long> selectedMonAnIds, String startDate, String endDate) {
        ChuongTrinhKhuyenMai khuyenMai = findKhuyenMaiById(maKM);
        if (khuyenMai == null) {
            return null;
        }

        validateTenKhuyenMai(tenKM);
        validatePhanTramGiam(phanTramGiam);

        String phamVi = APPLY_ALL;
        if (APPLY_ITEM.equalsIgnoreCase(phamViApDung)) {
            phamVi = APPLY_ITEM;
        }
        validateSelectedMonAn(phamVi, selectedMonAnIds);

        LocalDateTime ngayBatDau = null;
        if (startDate != null && !startDate.isBlank()) {
            ngayBatDau = LocalDate.parse(startDate).atTime(LocalTime.MIN);
        }

        LocalDateTime ngayKetThuc = null;
        if (endDate != null && !endDate.isBlank()) {
            ngayKetThuc = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        validateKhoangNgay(ngayBatDau, ngayKetThuc);

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

        List<ChuongTrinhKhuyenMai> khuyenMaiDangHoatDong = khuyenMaiRepository.findActivePromotions(LocalDateTime.now());
        if (khuyenMaiDangHoatDong == null || khuyenMaiDangHoatDong.isEmpty()) {
            return;
        }

        for (MonAn monAn : monAnList) {
            double phanTramGiamMax = 0;

            for (ChuongTrinhKhuyenMai khuyenMai : khuyenMaiDangHoatDong) {
                double phanTramGiam = 0;

                if (khuyenMai != null) {
                    if (APPLY_ALL.equals(khuyenMai.getPhamViApDung())) {
                        if (khuyenMai.getPhanTramGiam() == null) {
                            phanTramGiam = 0;
                        }
                        else {
                            phanTramGiam = khuyenMai.getPhanTramGiam();
                        }
                    }
                    else if (APPLY_ITEM.equals(khuyenMai.getPhamViApDung())
                            && khuyenMai.getChiTietKhuyenMai() != null) {
                        for (ChiTietKhuyenMai chiTiet : khuyenMai.getChiTietKhuyenMai()) {
                            if (chiTiet.getMonAn() != null
                                    && Objects.equals(chiTiet.getMonAn().getMaMon(), monAn.getMaMon())) {
                                if (chiTiet.getPhanTramGiam() != null) {
                                    phanTramGiam = chiTiet.getPhanTramGiam();
                                }
                                else if (khuyenMai.getPhanTramGiam() == null) {
                                    phanTramGiam = 0;
                                }
                                else {
                                    phanTramGiam = khuyenMai.getPhanTramGiam();
                                }
                                break;
                            }
                        }
                    }
                }

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

    private void updateChiTietKhuyenMai(ChuongTrinhKhuyenMai khuyenMai, String phamViApDung,
            List<Long> selectedMonAnIds) {
        if (!APPLY_ITEM.equals(phamViApDung) || selectedMonAnIds == null || selectedMonAnIds.isEmpty()) {
            khuyenMai.setChiTietKhuyenMai(new ArrayList<>());
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

        khuyenMai.setChiTietKhuyenMai(chiTietKhuyenMaiList);
    }

    private void resetKhuyenMai(List<MonAn> monAnList) {
        for (MonAn monAn : monAnList) {
            monAn.setHasGiamGia(false);
            monAn.setPhanTramGiam(0);
            monAn.setGiaGiam(monAn.getGia());
        }
    }

    private void validateTenKhuyenMai(String tenKM) {
        if (tenKM == null || tenKM.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống");
        }
    }

    private void validatePhanTramGiam(Double phanTramGiam) {
        if (phanTramGiam == null || phanTramGiam <= 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException("Phần trăm giảm phải lớn hơn 0 và không vượt quá 100");
        }
    }

    private void validateSelectedMonAn(String phamViApDung, List<Long> selectedMonAnIds) {
        if (APPLY_ITEM.equals(phamViApDung) && (selectedMonAnIds == null || selectedMonAnIds.isEmpty())) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một món");
        }
    }

    private void validateKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        if (ngayBatDau != null && ngayKetThuc != null && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc không được nhỏ hơn ngày bắt đầu");
        }
    }
}
