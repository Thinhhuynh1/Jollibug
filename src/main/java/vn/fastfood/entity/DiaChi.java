package vn.fastfood.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DIACHI")
@Data
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDC")
    private long maDC;

    @Column(name = "TenDiaChi")
    private String tenDiaChi;

    @Column(name = "TenNguoiNhan")
    private String tenNguoiNhan;

    @Column(name = "SDTNguoiNhan")
    private String sdtNguoiNhan;

    @Column(name = "DiaChiCuThe")
    private String diaChiCuThe;

    @Column(name = "TinhThanh")
    private String tinhThanh;

    @Column(name = "QuanHuyen")
    private String quanHuyen;

    @Column(name = "PhuongXa")
    private String phuongXa;

    @Column(name = "isDefault")
    private boolean defaultAddress = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "MaTK")
    private User user;
}
