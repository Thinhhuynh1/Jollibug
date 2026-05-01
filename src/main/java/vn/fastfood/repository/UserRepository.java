package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.Role;
import vn.fastfood.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findBySdt(String sdt);

    List<User> findByTrangThai(String trangThai);

    List<User> findAll();

    // find users by role name (TenVT) and status (TrangThai)
    @Query("""
                SELECT u FROM User u
                WHERE u.trangThai = :status
                AND (:role IS NULL OR :role = 'All' OR u.vaiTro.tenVt = :role)
                AND (:keyword IS NULL OR
                     LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    List<User> search(@Param("role") String role,
            @Param("keyword") String keyword,
            @Param("status") String status);

    // find users by role name only
    List<User> findByVaiTro_TenVt(String tenVt);

    void deleteById(long maTk);

}
