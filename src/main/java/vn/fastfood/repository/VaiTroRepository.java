package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.VaiTro;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    VaiTro findByTenVT(String maVT);

}
