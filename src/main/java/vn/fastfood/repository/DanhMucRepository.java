package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, String> {

}
