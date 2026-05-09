package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import vn.fastfood.entity.MonAn;

@Repository
public interface MonAnRepository extends JpaRepository<MonAn, Long> {

    @Query("""
                Select ma from MonAn ma
                where (:maDM is NULL OR ma.danhMuc.maDM = :maDM)
                and (:keyword is NULL OR :keyword = '')
            """)
    List<MonAn> findMonAn(@Param("maDM") Long maDM, @Param("keyword") String keyword);

    @Query("""
                Select ma from MonAn ma
                where (:maDM is NULL OR ma.danhMuc.maDM = :maDM)
                and (:keyword is NULL OR :keyword = ''
                     OR lower(ma.tenMon) like lower(concat('%', :keyword, '%'))
                     OR lower(ma.moTa) like lower(concat('%', :keyword, '%')))
                order by ma.gia asc
            """)
    List<MonAn> findMonAnPriceLow(@Param("maDM") Long maDM, @Param("keyword") String keyword);

    @Query("""
                Select ma from MonAn ma
                where (:maDM is NULL OR ma.danhMuc.maDM = :maDM)
                and (:keyword is NULL OR :keyword = ''
                     OR lower(ma.tenMon) like lower(concat('%', :keyword, '%'))
                     OR lower(ma.moTa) like lower(concat('%', :keyword, '%')))
                order by ma.gia desc
            """)
    List<MonAn> findMonAnPriceHigh(@Param("maDM") Long maDM, @Param("keyword") String keyword);

    @Query("""
                Select ma from MonAn ma
                where (:maDM is NULL OR ma.danhMuc.maDM = :maDM)
                and (:keyword is NULL OR :keyword = ''
                     OR lower(ma.tenMon) like lower(concat('%', :keyword, '%'))
                     OR lower(ma.moTa) like lower(concat('%', :keyword, '%')))
                order by ma.soLuongDaBan desc
            """)
    List<MonAn> findMonAnBestSeller(@Param("maDM") Long maDM, @Param("keyword") String keyword);
}
