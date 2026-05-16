package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DiaChi;

@Repository
public interface AddressRepository extends JpaRepository<DiaChi, Long> {
    long countByUser_MaTK(long maTK);

    DiaChi findByMaDC(long maDC);

    DiaChi findByUser_MaTKAndDefaultAddressTrue(long maTK);

    DiaChi findFirstByUser_MaTKOrderByMaDCAsc(long maTK);

    @Query(value = """
            SELECT *
            FROM DIACHI
            WHERE MATK = ?1
            ORDER BY ISDEFAULT DESC, MADC ASC
            """, nativeQuery = true)
    List<DiaChi> findByUserMaTKOrderByDefaultAddressDescMaDCAsc(long maTK);
}
