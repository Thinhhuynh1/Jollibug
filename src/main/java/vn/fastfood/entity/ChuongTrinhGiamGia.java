package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "CHUONGTRINHGIAMGIA")
@Data
public class ChuongTrinhGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCT")
    private Long maCT;

    @Column(name = "PhanTramGiam")
    private Double phanTramGiam;

    @Column(name = "TenCT", nullable = false, length = 100)
    private String tenCT;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @Transient
    public String getDiscountDisplay() {
        if (phanTramGiam == null) {
            return "-";
        }
        if (phanTramGiam % 1 == 0) {
            return String.format("%d%%", phanTramGiam.longValue());
        }
        return String.format("%s%%", phanTramGiam);
    }

    @Transient
    public String getThoiGianDisplay() {
        if (ngayBatDau == null && ngayKetThuc == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String start = ngayBatDau == null ? "?" : ngayBatDau.format(formatter);
        String end = ngayKetThuc == null ? "?" : ngayKetThuc.format(formatter);
        return start + " - " + end;
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
        return "Đang hoạt động";
    }

    @Transient
    public String getNgayBatDauValue() {
        return formatDateValue(ngayBatDau);
    }

    @Transient
    public String getNgayKetThucValue() {
        return formatDateValue(ngayKetThuc);
    }

    private String formatDateValue(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        try {
            return dateTime.toLocalDate().toString();
        } catch (DateTimeParseException ex) {
            return "";
        }
    }
}
