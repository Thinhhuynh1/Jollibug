package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.User;
import vn.fastfood.entity.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findBySdt(String sdt);

    List<User> findByTrangThai(UserStatus trangThai);

    List<User> findAll();

    // find users by role name (TenVT) and status (TrangThai)
    @Query("""
                SELECT u FROM User u
                WHERE u.trangThai = :status
                AND (:role IS NULL OR :role = 'All' OR u.vaiTro.tenVT = :role)
                AND (:keyword IS NULL OR
                     LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    List<User> search(@Param("role") String role,
            @Param("keyword") String keyword,
            @Param("status") UserStatus status);

    // find users by role name only
    List<User> findByVaiTro_TenVT(String tenVT);

    @Query("""
                SELECT count(u) FROM User u
                WHERE ( :role is NULL OR u.vaiTro.tenVT = :role)
                AND (:status is NULL OR u.trangThai = :status)
            """)
    long count(@Param("role") String role,
            @Param("status") UserStatus status);

    void deleteById(long maTK);

}
