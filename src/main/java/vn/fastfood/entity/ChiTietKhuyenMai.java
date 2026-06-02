package vn.fastfood.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CHITIETKHUYENMAI")
@Data
public class ChiTietKhuyenMai {
    @EmbeddedId
    private ChiTietKhuyenMaiId id = new ChiTietKhuyenMaiId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maKM")
    @JoinColumn(name = "MaKM")
    private ChuongTrinhKhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maMon")
    @JoinColumn(name = "MaMon")
    private MonAn monAn;

    @Column(name = "PhanTramGiam")
    private Double phanTramGiam;
}
