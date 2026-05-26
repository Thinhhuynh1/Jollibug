package vn.fastfood.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.YeuCauHoTro;
import vn.fastfood.repository.YeuCauHoTroRepository;

@Service
public class YeuCauHoTroService {

    @Autowired
    private YeuCauHoTroRepository yeuCauHoTroRepository;

    @Autowired
    private DataSource dataSource;

    public YeuCauHoTro createYeuCau(Long maTKKH, String tieuDe, String noiDung) {
        Long maYC = createSupportRequest(maTKKH, tieuDe, noiDung);
        return maYC == null ? null : yeuCauHoTroRepository.findById(maYC).orElse(null);
    }

    public YeuCauHoTro getYeuCau(Long maYC) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        return optional.orElse(null);
    }

    public YeuCauHoTro updateTrangThai(Long maYC, String trangThai) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        if (optional.isEmpty()) {
            return null;
        }

        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_UPDATE_SUPPORT_STATUS(?, ?)}")) {
            statement.setLong(1, maYC);
            statement.setString(2, trangThai);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }

        return yeuCauHoTroRepository.findById(maYC).orElse(null);
    }

    public YeuCauHoTro assignToStaff(Long maYC, Long maTKNV) {
        Optional<YeuCauHoTro> optional = yeuCauHoTroRepository.findById(maYC);
        if (optional.isEmpty()) {
            return null;
        }

        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_ASSIGN_SUPPORT_REQUEST(?, ?)}")) {
            statement.setLong(1, maYC);
            statement.setLong(2, maTKNV);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }

        return yeuCauHoTroRepository.findById(maYC).orElse(null);
    }

    public List<YeuCauHoTro> getYeuCauByKhachHang(Long maTKKH) {
        return yeuCauHoTroRepository.findByMaTKKH(maTKKH);
    }

    public List<YeuCauHoTro> getYeuCauByNhanVien(Long maTKNV) {
        return yeuCauHoTroRepository.findByMaTKNV(maTKNV);
    }

    public List<YeuCauHoTro> getPendingYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("PENDING");
    }

    public List<YeuCauHoTro> getProcessingYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("PROCESSING");
    }

    public List<YeuCauHoTro> getDoneYeuCau() {
        return yeuCauHoTroRepository.findByTrangThai("DONE");
    }

    public void deleteYeuCau(Long maYC) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_DELETE_SUPPORT_REQUEST(?)}")) {
            statement.setLong(1, maYC);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }

    private Long createSupportRequest(Long maTKKH, String tieuDe, String noiDung) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_CREATE_SUPPORT_REQUEST(?, ?, ?, ?)}")) {
            statement.setLong(1, maTKKH);
            statement.setString(2, tieuDe);
            statement.setString(3, noiDung);
            statement.registerOutParameter(4, Types.BIGINT);
            statement.execute();
            return statement.getLong(4);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }
}
