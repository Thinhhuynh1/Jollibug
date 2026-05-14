package vn.fastfood.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    private long maDH;
    private long maTKKH;
    private Long maTKNV;
    private Timestamp ngayDat;
    private Long maDC;
    private BigDecimal tongTienMon;
    private BigDecimal tienGiamGia;
    private BigDecimal thanhTien;
    private String trangThaiDon;
    private Long maGG;
    private String ghiChu;

    private String tenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;

    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiaoHang;

    private String maPT;
    private String tenPT;
    private String trangThaiTT;

    public Order() {}

    public long getMaDH() {
        return maDH;
    }

    public void setMaDH(long maDH) {
        this.maDH = maDH;
    }

    public long getMaTKKH() {
        return maTKKH;
    }

    public void setMaTKKH(long maTKKH) {
        this.maTKKH = maTKKH;
    }

    public Long getMaTKNV() {
        return maTKNV;
    }

    public void setMaTKNV(Long maTKNV) {
        this.maTKNV = maTKNV;
    }

    public Timestamp getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Timestamp ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Long getMaDC() {
        return maDC;
    }

    public void setMaDC(Long maDC) {
        this.maDC = maDC;
    }

    public BigDecimal getTongTienMon() {
        return tongTienMon;
    }

    public void setTongTienMon(BigDecimal tongTienMon) {
        this.tongTienMon = tongTienMon;
    }

    public BigDecimal getTienGiamGia() {
        return tienGiamGia;
    }

    public void setTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getTrangThaiDon() {
        return trangThaiDon;
    }

    public void setTrangThaiDon(String trangThaiDon) {
        this.trangThaiDon = trangThaiDon;
    }

    public Long getMaGG() {
        return maGG;
    }

    public void setMaGG(Long maGG) {
        this.maGG = maGG;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdtKhachHang() {
        return sdtKhachHang;
    }

    public void setSdtKhachHang(String sdtKhachHang) {
        this.sdtKhachHang = sdtKhachHang;
    }

    public String getEmailKhachHang() {
        return emailKhachHang;
    }

    public void setEmailKhachHang(String emailKhachHang) {
        this.emailKhachHang = emailKhachHang;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public String getSdtNguoiNhan() {
        return sdtNguoiNhan;
    }

    public void setSdtNguoiNhan(String sdtNguoiNhan) {
        this.sdtNguoiNhan = sdtNguoiNhan;
    }

    public String getDiaChiGiaoHang() {
        return diaChiGiaoHang;
    }

    public void setDiaChiGiaoHang(String diaChiGiaoHang) {
        this.diaChiGiaoHang = diaChiGiaoHang;
    }

    public String getMaPT() {
        return maPT;
    }

    public void setMaPT(String maPT) {
        this.maPT = maPT;
    }

    public String getTenPT() {
        return tenPT;
    }

    public void setTenPT(String tenPT) {
        this.tenPT = tenPT;
    }

    public String getTrangThaiTT() {
        return trangThaiTT;
    }

    public void setTrangThaiTT(String trangThaiTT) {
        this.trangThaiTT = trangThaiTT;
    }

    @Override
    public String toString() {
        return "Order{" +
                "maDH=" + maDH +
                ", maTKKH=" + maTKKH +
                ", ngayDat=" + ngayDat +
                ", thanhTien=" + thanhTien +
                ", trangThaiDon='" + trangThaiDon + '\'' +
                '}';
    }
}
