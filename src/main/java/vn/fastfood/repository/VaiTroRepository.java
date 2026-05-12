package vn.fastfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.VaiTro;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    @Query("""
            select vt from VaiTro vt
            where upper(trim(vt.tenVT)) = upper(trim(:tenVT))
            """)
    VaiTro findByTenVT(@Param("tenVT") String tenVT);

}
