package vn.fastfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.ChuongTrinhGiamGia;

@Repository
public interface ChuongTrinhGiamGiaRepository extends JpaRepository<ChuongTrinhGiamGia, Long> {

    @Query("select c from ChuongTrinhGiamGia c where :keyword is null or lower(c.tenCT) like lower(concat('%', :keyword, '%'))")
    List<ChuongTrinhGiamGia> searchByName(@Param("keyword") String keyword);

    @Query("select c from ChuongTrinhGiamGia c where c.ngayBatDau <= :now and c.ngayKetThuc >= :now")
    List<ChuongTrinhGiamGia> findActivePromotions(@Param("now") LocalDateTime now);
}
