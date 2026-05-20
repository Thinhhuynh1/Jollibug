package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        User findByEmail(String email);

        User findBySdt(String sdt);

        User findByMaTK(long maTK);

        List<User> findByTrangThai(String trangThai);

        List<User> findAll();

        // find users by role name (TenVT) and status (TrangThai)
        @Query("""
                            SELECT u FROM User u
                            WHERE u.trangThai = :status
                            AND (:role IS NULL OR UPPER(:role) = 'ALL' OR UPPER(u.vaiTro.tenVT) = UPPER(:role))
                            AND (:keyword IS NULL OR
                                 LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                 OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
                        """)
        List<User> search(@Param("role") String role,
                        @Param("keyword") String keyword,
                        @Param("status") String status);

        // find users by role name only
        List<User> findByVaiTro_TenVT(String tenVT);

        @Query("""
                            SELECT count(u) FROM User u
                            WHERE ( :role is NULL OR UPPER(u.vaiTro.tenVT) = UPPER(:role))
                            AND (:status is NULL OR u.trangThai = :status)
                        """)
        long count(@Param("role") String role,
                        @Param("status") String status);

        void deleteById(long maTK);

        // Đếm khách hàng mới trong khoảng thời gian
        @Query("""
                            SELECT COUNT(u) FROM User u
                            WHERE u.vaiTro.tenVT = 'CLIENT'
                            AND u.createdAt BETWEEN :fromDate AND :toDate
                        """)
        long countNewCustomers(@Param("fromDate") java.time.LocalDateTime fromDate,
                        @Param("toDate") java.time.LocalDateTime toDate);

        // Đếm tổng khách hàng active
        @Query("""
                            SELECT COUNT(u) FROM User u
                            WHERE u.vaiTro.tenVT = 'CLIENT'
                            AND u.trangThai = 'ACTIVE'
                        """)
        long countActiveCustomers();

}
