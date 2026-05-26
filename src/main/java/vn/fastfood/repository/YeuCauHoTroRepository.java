package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.YeuCauHoTro;

@Repository
public interface YeuCauHoTroRepository extends JpaRepository<YeuCauHoTro, Long> {

    /**
     * Tất cả yêu cầu của một khách hàng theo MaTK_KH.
     * Dùng @Query vì Spring Data không parse được tên method có
     * nhiều chữ hoa liên tiếp (maTKKH → PartTreeJpaQuery error).
     */
    @Query("SELECT y FROM YeuCauHoTro y WHERE y.maTKKH = :maTKKH ORDER BY y.createdAt DESC")
    List<YeuCauHoTro> findByMaTKKH(@Param("maTKKH") Long maTKKH);

    /**
     * Tất cả yêu cầu được giao cho một nhân viên theo MaTK_NV.
     */
    @Query("SELECT y FROM YeuCauHoTro y WHERE y.maTKNV = :maTKNV ORDER BY y.createdAt DESC")
    List<YeuCauHoTro> findByMaTKNV(@Param("maTKNV") Long maTKNV);

    /**
     * Tất cả yêu cầu theo trạng thái (PENDING / PROCESSING / DONE).
     */
    @Query("SELECT y FROM YeuCauHoTro y WHERE y.trangThai = :trangThai ORDER BY y.createdAt DESC")
    List<YeuCauHoTro> findByTrangThai(@Param("trangThai") String trangThai);

    /**
     * Tất cả yêu cầu còn active (PENDING + PROCESSING) – dùng cho sidebar staff.
     */
    @Query("SELECT y FROM YeuCauHoTro y WHERE y.trangThai <> 'DONE' ORDER BY y.createdAt DESC")
    List<YeuCauHoTro> findActiveRequests();
}
