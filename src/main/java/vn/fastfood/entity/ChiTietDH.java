package vn.fastfood.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CHITIETDH")
@IdClass(ChiTietDHId.class)
@Data
public class ChiTietDH {

    @Id
    @Column(name = "MaDH")
    private Long maDH;

    @Id
    @Column(name = "MaMon")
    private Long maMon;

    @Column(name = "TenMon")
    private String tenMon;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia")
    private Long donGia;

    @Column(name = "ThanhTien")
    private Long thanhTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDH", insertable = false, updatable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaMon", insertable = false, updatable = false)
    private MonAn monAn;
}
