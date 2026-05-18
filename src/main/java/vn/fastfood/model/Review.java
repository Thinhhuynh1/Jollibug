package vn.fastfood.model;

import java.sql.Timestamp;

public class Review {
    private long maDG;
    private long maTKKH;
    private long maMon;
    private long maDH;
    private int sao;
    private String noiDung;
    private Timestamp ngayDG;
    private String tenMon;
    private String imageUrl;
    private String tenKhachHang;
    private String emailKhachHang;

    public Review() {
    }

    public long getMaDG() {
        return maDG;
    }

    public void setMaDG(long maDG) {
        this.maDG = maDG;
    }

    public long getMaTKKH() {
        return maTKKH;
    }

    public void setMaTKKH(long maTKKH) {
        this.maTKKH = maTKKH;
    }

    public long getMaMon() {
        return maMon;
    }

    public void setMaMon(long maMon) {
        this.maMon = maMon;
    }

    public long getMaDH() {
        return maDH;
    }

    public void setMaDH(long maDH) {
        this.maDH = maDH;
    }

    public int getSao() {
        return sao;
    }

    public void setSao(int sao) {
        this.sao = sao;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Timestamp getNgayDG() {
        return ngayDG;
    }

    public void setNgayDG(Timestamp ngayDG) {
        this.ngayDG = ngayDG;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getEmailKhachHang() {
        return emailKhachHang;
    }

    public void setEmailKhachHang(String emailKhachHang) {
        this.emailKhachHang = emailKhachHang;
    }
}
