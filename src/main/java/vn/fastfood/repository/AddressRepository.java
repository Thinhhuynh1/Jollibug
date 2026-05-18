package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DiaChi;

@Repository
public interface AddressRepository extends JpaRepository<DiaChi, Long> {

        //     @Query("""
        //                 select count(ma)
        //                 from  MonAn ma
        //                 where ma.danhMuc.maDM = :categoryID
        //                 """)
        // long countMonAn(@Param("categoryID") long categoryID);\
    @Query("""
            select count(1)
            from DiaChi dc
            where dc.user.maTK = :maTK
            """)
    long countByUser_MaTK(@Param("maTK") long maTK);

    @Query("SELECT d FROM DiaChi d WHERE d.maDC = :maDC")
    DiaChi findByMaDC(long maDC);


    @Query("SELECT d FROM DiaChi d WHERE d.user.maTK = :maTK AND d.defaultAddress = TRUE")
    DiaChi findByUser_MaTKAndDefaultAddressTrue(long maTK);

    @Query("SELECT d FROM DiaChi d WHERE d.user.maTK = :maTK ORDER BY d.maDC asc")
    DiaChi findFirstByUser_MaTKOrderByMaDCAsc(long maTK);

    @Query(value = """
            SELECT *
            FROM DIACHI
            WHERE MATK = ?1
            ORDER BY ISDEFAULT DESC, MADC ASC
            """, nativeQuery = true)
    List<DiaChi> findByUserMaTKOrderByDefaultAddressDescMaDCAsc(long maTK);
}
