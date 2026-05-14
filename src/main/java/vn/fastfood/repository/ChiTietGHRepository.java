package vn.fastfood.repository;

import vn.fastfood.entity.ChiTietGH;
import vn.fastfood.entity.ChiTietGHId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiTietGHRepository
        extends JpaRepository<ChiTietGH, ChiTietGHId> {

    Optional<ChiTietGH> findByMaGHAndMaMon(
            Long maGH,
            Long maMon
    );
}