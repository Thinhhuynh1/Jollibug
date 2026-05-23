package vn.fastfood.model;

public class CartItem {
    private long maGH;
    private long maMon;
    private String tenMon;
    private int soLuong;
    private double donGia;
    private double donGiaGoc;
    private double thanhTien;
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

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getDonGiaGoc() {
        return donGiaGoc > 0 ? donGiaGoc : donGia;
    }

    public void setDonGiaGoc(double donGiaGoc) {
        this.donGiaGoc = donGiaGoc;
    }
}
