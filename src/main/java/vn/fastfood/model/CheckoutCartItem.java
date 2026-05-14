package vn.fastfood.model;

import java.math.BigDecimal;

public class CheckoutCartItem {
    private long maMon;
    private String tenMon;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public CheckoutCartItem() {
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
}