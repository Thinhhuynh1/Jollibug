package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "MONAN")
@Data
public class MonAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaMon")
    private long maMon;

    @Column(name = "TenMon")
    @Nationalized
    private String tenMon;

    @Column(name = "MoTa")
    @Nationalized
    private String moTa;

    @Column(name = "DonVi")
    @Nationalized
    private String donVi = "phần";

    @Column(name = "Gia")
    private long gia;

    @Column(name = "SoLuongTon")
    private long soLuongTon;

    @Column(name = "SoLuongDaBan")
    private long soLuongDaBan;

    @Column(name = "image_url")
    private String img;

    @Column(name = "IsAvailable")
    private boolean available = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public String getCreatedAtDisplay() {
        return createdAt == null ? "-" : createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Transient
    public String getUpdatedAtDisplay() {
        return updatedAt == null ? "-" : updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @ManyToOne
    @JoinColumn(name = "MaDM")
    private DanhMuc danhMuc;

    @Column(name = "GiaGiam")
    private long giaGiam;

    @Transient
    private double phanTramGiam;

    @Transient
    private boolean hasGiamGia;

    public long getGiaGiam() {
        if (hasGiamGia) {
            return giaGiam;
        }
        return giaGiam > 0 ? giaGiam : gia;
    }
}
