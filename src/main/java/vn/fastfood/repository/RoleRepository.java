package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByTenVt(String tenVt);
}
