package vn.fastfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.ChuongTrinhKhuyenMai;

@Repository
public interface ChuongTrinhKhuyenMaiRepository extends JpaRepository<ChuongTrinhKhuyenMai, Long> {

    @Query("select c from ChuongTrinhKhuyenMai c where :keyword is null or lower(c.tenKM) like lower(concat('%', :keyword, '%'))")
    List<ChuongTrinhKhuyenMai> searchByName(@Param("keyword") String keyword);

    @Query("""
            select c
            from ChuongTrinhKhuyenMai c
            where (c.ngayBatDau is null or c.ngayBatDau <= :now)
              and (c.ngayKetThuc is null or c.ngayKetThuc >= :now)
            """)
    List<ChuongTrinhKhuyenMai> findActivePromotions(@Param("now") LocalDateTime now);
}
