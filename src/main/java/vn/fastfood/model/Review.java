package vn.fastfood.model;

import java.sql.Timestamp;

import vn.fastfood.entity.User;

public class Review {
    private long maDG;
    private long maTKKH;
    private long maMon;
    private long maDH;
    private int sao;
    private String noiDung;
    private String anhDG;
    private String tenMon;
    private String img;
    private Timestamp ngayDG;
    private User khachHang;

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

    public String getAnhDG() {
        return anhDG;
    }

    public void setAnhDG(String anhDG) {
        this.anhDG = anhDG;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Timestamp getNgayDG() {
        return ngayDG;
    }

    public void setNgayDG(Timestamp ngayDG) {
        this.ngayDG = ngayDG;
    }

    public User getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(User khachHang) {
        this.khachHang = khachHang;
    }
}
