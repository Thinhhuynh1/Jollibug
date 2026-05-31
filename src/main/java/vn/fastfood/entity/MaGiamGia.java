package vn.fastfood.entity;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "MAGIAMGIA")
@Data
public class MaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGG")
    private Long maGG;

    @Column(name = "MACODE", length = 50, nullable = false)
    private String tenMa;

    @Column(name = "LoaiGiam", length = 20)
    private String loaiGiam; // PERCENTAGE hoặc AMOUNT

    @Column(name = "MucGiam")
    private Double mucGiam;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "DieuKien")
    private Double dieuKien;

    @Transient
    public String getMinimumOrderDisplay() {
        if (dieuKien == null || dieuKien <= 0) {
            return null;
        }
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(dieuKien) + "đ";
    }

    @Transient
    public String getStatus() {
        if (ngayBatDau == null || ngayKetThuc == null) {
            return "Chưa xác định";
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ngayBatDau)) {
            return "Sắp diễn ra";
        }
        if (now.isAfter(ngayKetThuc)) {
            return "Đã kết thúc";
        }
        if (soLuong != null && soLuong <= 0) {
            return "Hết lượt dùng";
        }
        return "Đang hoạt động";
    }

    @Transient
    public String getNgayBatDauValue() {
        if (ngayBatDau == null)
            return "";
        return ngayBatDau.toLocalDate().toString();
    }

    @Transient
    public String getNgayKetThucValue() {
        if (ngayKetThuc == null)
            return "";
        return ngayKetThuc.toLocalDate().toString();
    }

    @Transient
    public String getDiscountDisplay() {
        if (mucGiam == null)
            return "0";
        if ("PERCENTAGE".equals(loaiGiam)) {
            if (mucGiam % 1 == 0) {
                return String.format("%d%%", mucGiam.longValue());
            }
            return String.format("%s%%", mucGiam);
        } else {
            NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return vnFormat.format(mucGiam);
        }
    }
}
