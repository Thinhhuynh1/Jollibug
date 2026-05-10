package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {

        @Query("""
                        Select d from DanhMuc d
                        where (d.maDM = :categoryID)
                        """)
        DanhMuc findDanhMuc(@Param("categoryID") Long categoryID);

        @Query("select d from DanhMuc d")
        List<DanhMuc> findListDanhMuc();

        @Query("""
                        select count(ma)
                        from  MonAn ma
                        where ma.danhMuc.maDM = :categoryID
                        """)
        long countMonAn(@Param("categoryID") long categoryID);
}
