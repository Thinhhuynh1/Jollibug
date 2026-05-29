package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.ChiTietKhuyenMai;
import vn.fastfood.entity.ChiTietKhuyenMaiId;

@Repository
public interface ChiTietKhuyenMaiRepository extends JpaRepository<ChiTietKhuyenMai, ChiTietKhuyenMaiId> {
}
