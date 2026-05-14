package vn.fastfood.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.YeuCauHoTroRepository;

@Service
public class YeuCauHoTroService {

    @Autowired
    private YeuCauHoTroRepository yeuCauHoTroRepository;

    /** Tạo yêu cầu hỗ trợ mới */
    public YeuCauHoTro createYeuCau(Long maTKKH, String tieuDe, String noiDung) {
        YeuCauHoTro yeuCau = new YeuCauHoTro(maTKKH, tieuDe, noiDung);
        return yeuCauHoTroRepository.save(yeuCau);
    }

    /** Lấy chi tiết yêu cầu */
    public YeuCauHoTro getYeuCau(Long maYC) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        return optional.orElse(null);
    }

    /** Cập nhật trạng thái yêu cầu */
    public YeuCauHoTro updateTrangThai(Long maYC, String trangThai) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        if (optional.isPresent()) {
            YeuCauHoTro yeuCau = optional.get();
            yeuCau.setTrangThai(trangThai);
            return yeuCauHoTroRepository.save(yeuCau);
        }
        return null;
    }

    /** Giao yêu cầu cho nhân viên */
    public YeuCauHoTro assignToStaff(Long maYC, Long maTKNV) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        if (optional.isPresent()) {
            YeuCauHoTro yeuCau = optional.get();
            yeuCau.setMaTKNV(maTKNV);
            yeuCau.setTrangThai("Processing");
            return yeuCauHoTroRepository.save(yeuCau);
        }
        return null;
    }

    /** Lấy tất cả yêu cầu của khách hàng */
    public List<YeuCauHoTro> getYeuCauByKhachHang(Long maTKKH) {
        return yeuCauHoTroRepository.findByMaTKKH(maTKKH);
    }

    /** Lấy tất cả yêu cầu được giao cho nhân viên */
    public List<YeuCauHoTro> getYeuCauByNhanVien(Long maTKNV) {
        return yeuCauHoTroRepository.findByMaTKNV(maTKNV);
    }

    /** Lấy tất cả yêu cầu chưa được xử lý */
    public List<YeuCauHoTro> getPendingYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("Pending");
    }

    /** Lấy tất cả yêu cầu đang xử lý */
    public List<YeuCauHoTro> getProcessingYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("Processing");
    }

    /** Lấy tất cả yêu cầu đã xong */
    public List<YeuCauHoTro> getDoneYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("Done");
    }

    /** Xóa yêu cầu */
    public void deleteYeuCau(Long maYC) {
        yeuCauHoTroRepository.deleteById(maYC);
    }
}
