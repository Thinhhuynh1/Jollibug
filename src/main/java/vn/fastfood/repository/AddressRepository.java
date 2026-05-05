package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DiaChi;

@Repository
public interface AddressRepository extends JpaRepository<DiaChi, Long> {
    long countByUser_MaTK(long maTK);

    DiaChi findByMaDC(long maDC);

    DiaChi findByUser_MaTKAndDefaultAddressTrue(long maTK);

    DiaChi findFirstByUser_MaTKOrderByMaDCAsc(long maTK);
}
