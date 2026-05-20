package vn.fastfood.model;

import java.math.BigDecimal;

public class CartItem {
    private long maGH;
    private long maMon;
    private String tenMon;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal donGiaGoc;
    private BigDecimal thanhTien;
    private String imageUrl;

    public CartItem() {
    }

    public long getMaGH() {
        return maGH;
    }

    public void setMaGH(long maGH) {
        this.maGH = maGH;
    }

    public long getMaMon() {
        return maMon;
    }

    public void setMaMon(long maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getDonGiaGoc() {
        return donGiaGoc != null ? donGiaGoc : donGia;
    }

    public void setDonGiaGoc(BigDecimal donGiaGoc) {
        this.donGiaGoc = donGiaGoc;
    }
}