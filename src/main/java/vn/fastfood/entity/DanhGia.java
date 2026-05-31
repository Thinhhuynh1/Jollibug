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
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "DANHGIA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_DANHGIA_ORDER_ITEM", columnNames = { "MaDH", "MaMon", "MaTK_KH" })
})
@Data
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDG")
    private Long maDG;

    @Column(name = "MaTK_KH", nullable = false)
    private Long maTKKH;

    @ManyToOne
    @JoinColumn(name = "MaMon", nullable = false)
    private MonAn monAn;

    @ManyToOne
    @JoinColumn(name = "MaDH", nullable = false)
    private DonHang donHang;

    @Column(name = "Sao", nullable = false)
    private int sao;

    @Column(name = "NoiDung")
    @Nationalized
    private String noiDung;

    @CreationTimestamp
    @Column(name = "NgayDG", updatable = false)
    private LocalDateTime ngayDG;

    @UpdateTimestamp
    @Column(name = "NgayCapNhat")
    private LocalDateTime ngayCapNhat;

    @Transient
    public String getNgayDGDisplay() {
        return ngayDG == null ? "-" : ngayDG.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Transient
    public String getNgayCapNhatDisplay() {
        return ngayCapNhat == null ? "-" : ngayCapNhat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
