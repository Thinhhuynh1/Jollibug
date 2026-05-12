package vn.fastfood.model;

import java.sql.Timestamp;

public class OrderStatusHistory {
    private long maLS;
    private long maDH;
    private String trangThaiCu;
    private String trangThaiMoi;
    private String nguoiThucHienLoai;
    private Long maNguoiThucHien;
    private String lyDo;
    private Timestamp thoiGian;

    public OrderStatusHistory() {
    }

    public long getMaLS() {
        return maLS;
    }

    public void setMaLS(long maLS) {
        this.maLS = maLS;
    }

    public long getMaDH() {
        return maDH;
    }

    public void setMaDH(long maDH) {
        this.maDH = maDH;
    }

    public String getTrangThaiCu() {
        return trangThaiCu;
    }

    public void setTrangThaiCu(String trangThaiCu) {
        this.trangThaiCu = trangThaiCu;
    }

    public String getTrangThaiMoi() {
        return trangThaiMoi;
    }

    public void setTrangThaiMoi(String trangThaiMoi) {
        this.trangThaiMoi = trangThaiMoi;
    }

    public String getNguoiThucHienLoai() {
        return nguoiThucHienLoai;
    }

    public void setNguoiThucHienLoai(String nguoiThucHienLoai) {
        this.nguoiThucHienLoai = nguoiThucHienLoai;
    }

    public Long getMaNguoiThucHien() {
        return maNguoiThucHien;
    }

    public void setMaNguoiThucHien(Long maNguoiThucHien) {
        this.maNguoiThucHien = maNguoiThucHien;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public Timestamp getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Timestamp thoiGian) {
        this.thoiGian = thoiGian;
    }
}
