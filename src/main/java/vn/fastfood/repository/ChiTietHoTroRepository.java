package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.ChiTietHoTro;

@Repository
public interface ChiTietHoTroRepository extends JpaRepository<ChiTietHoTro, Long> {

    /** Lấy toàn bộ tin nhắn của một yêu cầu, sắp theo thời gian tăng dần */
    List<ChiTietHoTro> findByYeuCau_MaYCOrderByNgayGuiAsc(Long maYC);

    /** Lấy tin nhắn cuối cùng của một yêu cầu (dùng cho preview sidebar staff) */
    ChiTietHoTro findTop1ByYeuCau_MaYCOrderByNgayGuiDesc(Long maYC);

    /** Đếm số tin nhắn chưa đọc của một yêu cầu (có thể mở rộng sau) */
    long countByYeuCau_MaYC(Long maYC);
}
