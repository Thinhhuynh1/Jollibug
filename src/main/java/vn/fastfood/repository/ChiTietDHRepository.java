package vn.fastfood.repository;

import vn.fastfood.entity.ChiTietDH;
import vn.fastfood.entity.ChiTietDHId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDHRepository extends JpaRepository<ChiTietDH, ChiTietDHId> {

    List<ChiTietDH> findByMaDH(Long maDH);
}
