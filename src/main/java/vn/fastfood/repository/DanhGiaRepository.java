package vn.fastfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DanhGia;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {

    List<DanhGia> findByMaTKKHOrderByNgayDGDesc(Long maTKKH);

    List<DanhGia> findByDonHang_MaDHAndMaTKKH(Long maDH, Long maTKKH);

    Optional<DanhGia> findByMaDGAndMaTKKH(Long maDG, Long maTKKH);

    boolean existsByDonHang_MaDHAndMonAn_MaMonAndMaTKKH(Long maDH, Long maMon, Long maTKKH);
}
