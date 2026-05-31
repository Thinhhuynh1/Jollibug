package vn.fastfood.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    // === THỐNG KÊ DOANH THU ===

    // Tổng doanh thu theo khoảng thời gian (chỉ đơn DELIVERED)
    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d " +
           "WHERE d.trangThai = 'DELIVERED' " +
           "AND d.ngayDat BETWEEN :fromDate AND :toDate")
    Long sumRevenue(@Param("fromDate") LocalDateTime fromDate,
                    @Param("toDate") LocalDateTime toDate);

    // Doanh thu theo từng ngày (cho biểu đồ) - Oracle native query
    @Query(value = "SELECT TRUNC(d.NGAYDAT) AS ngay, COALESCE(SUM(d.TONGTIEN), 0) AS doanhThu " +
               "FROM DONHANG d " +
               "WHERE d.TRANGTHAI = 'DELIVERED' " +
               "AND d.NGAYDAT BETWEEN :fromDate AND :toDate " +
               "GROUP BY TRUNC(d.NGAYDAT) " +
               "ORDER BY TRUNC(d.NGAYDAT)",
       nativeQuery = true)
List<Object[]> revenueByDay(@Param("fromDate") LocalDateTime fromDate,
                            @Param("toDate") LocalDateTime toDate);

    // Doanh thu theo tháng trong năm - Oracle native query
    @Query(value = "SELECT EXTRACT(MONTH FROM d.NGAYDAT) AS thang, " +
               "COALESCE(SUM(d.TONGTIEN), 0) AS doanhThu " +
               "FROM DONHANG d " +
               "WHERE d.TRANGTHAI = 'DELIVERED' " +
               "AND EXTRACT(YEAR FROM d.NGAYDAT) = :year " +
               "GROUP BY EXTRACT(MONTH FROM d.NGAYDAT) " +
               "ORDER BY EXTRACT(MONTH FROM d.NGAYDAT)",
       nativeQuery = true)
List<Object[]> revenueByMonth(@Param("year") int year);
    // === THỐNG KÊ ĐƠN HÀNG ===

    // Đếm đơn hàng theo trạng thái
    long countByTrangThai(String trangThai);

    // Đếm tổng đơn trong khoảng thời gian
    @Query("SELECT COUNT(d) FROM DonHang d " +
           "WHERE d.ngayDat BETWEEN :fromDate AND :toDate")
    long countByDateRange(@Param("fromDate") LocalDateTime fromDate,
                          @Param("toDate") LocalDateTime toDate);

    // Đếm đơn theo trạng thái + khoảng thời gian (cho biểu đồ donut)
    @Query("SELECT d.trangThai, COUNT(d) FROM DonHang d " +
           "WHERE d.ngayDat BETWEEN :fromDate AND :toDate " +
           "GROUP BY d.trangThai")
    List<Object[]> countGroupByStatus(@Param("fromDate") LocalDateTime fromDate,
                                      @Param("toDate") LocalDateTime toDate);

    // Số đơn theo ngày (cho biểu đồ) - Oracle native query
    @Query(value = "SELECT TRUNC(d.NGAYDAT) AS ngay, COUNT(*) AS soDon " +
                   "FROM DONHANG d " +
                   "WHERE d.NGAYDAT BETWEEN :fromDate AND :toDate " +
                   "GROUP BY TRUNC(d.NGAYDAT) " +
                   "ORDER BY TRUNC(d.NGAYDAT)",
           nativeQuery = true)
    List<Object[]> countByDay(@Param("fromDate") LocalDateTime fromDate,
                              @Param("toDate") LocalDateTime toDate);

    // Giá trị trung bình đơn hàng (chỉ đơn DELIVERED)
    @Query("SELECT COALESCE(AVG(d.tongTien), 0) FROM DonHang d " +
           "WHERE d.trangThai = 'DELIVERED' " +
           "AND d.ngayDat BETWEEN :fromDate AND :toDate")
    Double avgOrderValue(@Param("fromDate") LocalDateTime fromDate,
                         @Param("toDate") LocalDateTime toDate);

    // === THỐNG KÊ KHÁCH HÀNG ===

    // Đếm khách hàng có đặt đơn trong khoảng thời gian
    @Query("SELECT COUNT(DISTINCT d.user.maTK) FROM DonHang d " +
           "WHERE d.ngayDat BETWEEN :fromDate AND :toDate")
    long countDistinctCustomers(@Param("fromDate") LocalDateTime fromDate,
                                @Param("toDate") LocalDateTime toDate);

    // Top khách hàng theo tổng chi tiêu (chỉ đơn DELIVERED)
    @Query("SELECT d.user, SUM(d.tongTien), COUNT(d) FROM DonHang d " +
           "WHERE d.trangThai = 'DELIVERED' " +
           "GROUP BY d.user " +
           "ORDER BY SUM(d.tongTien) DESC")
    List<Object[]> findTopCustomers(Pageable pageable);

    // Đơn hàng gần đây
    @Query("SELECT d FROM DonHang d ORDER BY d.ngayDat DESC")
    List<DonHang> findRecentOrders(Pageable pageable);

    List<DonHang> findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(Long maTK, String trangThai);

    List<DonHang> findByUser_MaTKOrderByNgayDatDesc(Long maTK);
}
