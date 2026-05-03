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
