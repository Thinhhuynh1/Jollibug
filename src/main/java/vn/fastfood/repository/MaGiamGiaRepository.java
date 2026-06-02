package vn.fastfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.MaGiamGia;

@Repository
public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {

    @Query("select m from MaGiamGia m where :keyword is null or " +
           "lower(m.tenMa) like lower(concat('%', :keyword, '%')) or " +
           "lower(m.moTa) like lower(concat('%', :keyword, '%')) or " +
           "lower(m.loaiGiam) like lower(concat('%', :keyword, '%'))")
    List<MaGiamGia> searchByCode(@Param("keyword") String keyword);

    Optional<MaGiamGia> findByTenMa(String tenMa);

    @Query("select count(d) from DonHang d where d.maGiamGia.maGG = :couponId")
    long countOrdersUsingCoupon(@Param("couponId") Long couponId);
}
