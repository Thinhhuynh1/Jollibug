package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DONHANG")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDH")
    private Long maDH;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTK_KH")
    private User user;

    @Column(name = "ThanhTien")
    private Long tongTien;

    // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
    @Column(name = "TrangThaiDon")
    private String trangThai;

    @Column(name = "GhiChu")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "NgayDat", updatable = false)
    private LocalDateTime ngayDat;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "donHang", fetch = FetchType.LAZY)
    private List<ChiTietDH> chiTietDHList;

    @Transient
    public String getNgayDatDisplay() {
        return ngayDat == null ? "-" : ngayDat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Transient
    public String getUpdatedAtDisplay() {
        return updatedAt == null ? "-" : updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
