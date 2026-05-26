package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "KHUYENMAI")
@Data
public class ChuongTrinhKhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKM")
    private Long maKM;

    @Column(name = "PhanTramGiam")
    private Double phanTramGiam;

    @Column(name = "TenKM", nullable = false, length = 100)
    private String tenKM;

    @Column(name = "PhamViApDung", length = 20)
    private String phamViApDung;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChiTietKhuyenMai> chiTietKhuyenMais;

    @Transient
    public List<Long> getMonAnDuocApDungIds() {
        if (chiTietKhuyenMais == null || chiTietKhuyenMais.isEmpty()) {
            return List.of();
        }
        return chiTietKhuyenMais.stream()
                .map(ChiTietKhuyenMai::getMonAn)
                .filter(Objects::nonNull)
                .map(MonAn::getMaMon)
                .toList();
    }
}
