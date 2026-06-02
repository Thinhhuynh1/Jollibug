package vn.fastfood.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.fastfood.entity.DiaChi;
import vn.fastfood.repository.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DataSource dataSource;

    private long createAddress(DiaChi diaChi) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_CREATE_ADDRESS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setLong(1, diaChi.getUser().getMaTK());
            statement.setString(2, diaChi.getTenDiaChi());
            statement.setString(3, diaChi.getTenNguoiNhan());
            statement.setString(4, diaChi.getSdtNguoiNhan());
            statement.setString(5, diaChi.getDiaChiCuThe());
            statement.setString(6, diaChi.getTinhThanh());
            statement.setString(7, diaChi.getQuanHuyen());
            statement.setString(8, diaChi.getPhuongXa());
            statement.setInt(9, diaChi.isDefaultAddress() ? 1 : 0);
            statement.registerOutParameter(10, Types.BIGINT);
            statement.execute();
            return statement.getLong(10);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }

    private void updateAddress(DiaChi diaChi) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_UPDATE_ADDRESS(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setLong(1, diaChi.getMaDC());
            statement.setString(2, diaChi.getTenDiaChi());
            statement.setString(3, diaChi.getTenNguoiNhan());
            statement.setString(4, diaChi.getSdtNguoiNhan());
            statement.setString(5, diaChi.getDiaChiCuThe());
            statement.setString(6, diaChi.getTinhThanh());
            statement.setString(7, diaChi.getQuanHuyen());
            statement.setString(8, diaChi.getPhuongXa());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }

    public DiaChi findDiaChi(long maDC) {
        return this.addressRepository.findByMaDC(maDC);
    }

    public boolean hasAddress(long maTK) {
        return this.addressRepository.countByUser_MaTK(maTK) > 0;
    }

    public DiaChi saveDiaChi(DiaChi diaChi) {
        if (diaChi.getMaDC() > 0) {
            updateAddress(diaChi);
            return this.addressRepository.findByMaDC(diaChi.getMaDC());
        }

        long maDC = createAddress(diaChi);
        return this.addressRepository.findByMaDC(maDC);
    }

    public boolean setDefaultAddress(long maTK, long maDC) {
        DiaChi selectedAddress = this.addressRepository.findByMaDC(maDC);
        if (selectedAddress == null || selectedAddress.getUser() == null
                || selectedAddress.getUser().getMaTK() != maTK) {
            return false;
        }

        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_SET_DEFAULT_ADDRESS(?, ?)}")) {
            statement.setLong(1, maTK);
            statement.setLong(2, maDC);
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }

    @Transactional
    public boolean deleteDiaChi(long maTK, long maDC) {
        DiaChi address = this.addressRepository.findByMaDC(maDC);
        if (address == null || address.getUser() == null || address.getUser().getMaTK() != maTK) {
            return false;
        }

        boolean wasDefault = address.isDefaultAddress();

        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_DELETE_ADDRESS(?)}")) {
            statement.setLong(1, maDC);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }

        if (wasDefault) {
            DiaChi nextDefault = this.addressRepository.findFirstByUser_MaTKOrderByMaDCAsc(maTK);
            if (nextDefault != null) {
                try (Connection connection = dataSource.getConnection();
                     CallableStatement statement = connection.prepareCall("{call PROC_SET_DEFAULT_ADDRESS(?, ?)}")) {
                    statement.setLong(1, maTK);
                    statement.setLong(2, nextDefault.getMaDC());
                    statement.execute();
                } catch (SQLException e) {
                    throw new RuntimeException("Không thể chạy procedure", e);
                }
            }
        }

        return true;
    }
}
