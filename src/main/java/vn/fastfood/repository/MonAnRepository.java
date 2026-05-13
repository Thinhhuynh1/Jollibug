package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.MonAn;

@Repository
public interface MonAnRepository extends JpaRepository<MonAn, Long> {

}
