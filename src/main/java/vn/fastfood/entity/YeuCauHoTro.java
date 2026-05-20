package vn.fastfood.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

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

/**
 * Yêu cầu hỗ trợ (YEUCAUHOTRO)
 * Một yêu cầu = một "phòng chat" giữa Client và Staff.
 * TrangThai: Pending | Processing | Done
 */
@Entity
@Table(name = "YEUCAUHOTRO")
public class YeuCauHoTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaYC")
    private Long maYC;

    // ---- Raw FK columns (để Service dùng setMaTKKH / setMaTKNV) ----

    /** FK sang USERS (khách hàng) – đọc/ghi trực tiếp */
    @Column(name = "MaTK_KH", nullable = false)
    private Long maTKKH;

    /** FK sang USERS (nhân viên) – null khi chưa có ai nhận */
    @Column(name = "MaTK_NV")
    private Long maTKNV;

    // ---- Lazy join để lấy thông tin đầy đủ khi cần ----

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTK_KH", insertable = false, updatable = false)
    private User khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTK_NV", insertable = false, updatable = false)
    private User nhanVien;

    @Column(name = "TieuDe", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "NoiDung", length = 4000)
    private String noiDung;

    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai = "Pending";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Danh sách tin nhắn thuộc yêu cầu này */
    @OneToMany(mappedBy = "yeuCau", fetch = FetchType.LAZY)
    private List<ChiTietHoTro> chiTiet;

    // ---- Constructors ----

    public YeuCauHoTro() {
    }

    /** Constructor dùng cho YeuCauHoTroService.createYeuCau() */
    public YeuCauHoTro(Long maTKKH, String tieuDe, String noiDung) {
        this.maTKKH = maTKKH;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.trangThai = "Pending";
    }

    // ---- Getters & Setters ----

    public Long getMaYC() {
        return maYC;
    }

    public void setMaYC(Long maYC) {
        this.maYC = maYC;
    }

    public Long getMaTKKH() {
        return maTKKH;
    }

    public void setMaTKKH(Long maTKKH) {
        this.maTKKH = maTKKH;
    }

    public Long getMaTKNV() {
        return maTKNV;
    }

    public void setMaTKNV(Long maTKNV) {
        this.maTKNV = maTKNV;
    }

    public User getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(User khachHang) {
        this.khachHang = khachHang;
        if (khachHang != null)
            this.maTKKH = khachHang.getMaTK();
    }

    public User getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(User nhanVien) {
        this.nhanVien = nhanVien;
        if (nhanVien != null)
            this.maTKNV = nhanVien.getMaTK();
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ChiTietHoTro> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<ChiTietHoTro> chiTiet) {
        this.chiTiet = chiTiet;
    }

    /** Helper cho JSP – JSTL fmt:formatDate không hỗ trợ LocalDateTime */
    @Transient

    public String getCreatedAtDisplay() {

        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }
}