package vn.fastfood.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private long maTT;
    private long maDH;
    private String maPT;
    private Timestamp ngayTT;
    private BigDecimal soTien;
    private String trangThaiTT;

    public Payment() {
    }

    public long getMaTT() {
        return maTT;
    }

    public void setMaTT(long maTT) {
        this.maTT = maTT;
    }

    public long getMaDH() {
        return maDH;
    }

    public void setMaDH(long maDH) {
        this.maDH = maDH;
    }

    public String getMaPT() {
        return maPT;
    }

    public void setMaPT(String maPT) {
        this.maPT = maPT;
    }

    public Timestamp getNgayTT() {
        return ngayTT;
    }

    public void setNgayTT(Timestamp ngayTT) {
        this.ngayTT = ngayTT;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getTrangThaiTT() {
        return trangThaiTT;
    }

    public void setTrangThaiTT(String trangThaiTT) {
        this.trangThaiTT = trangThaiTT;
    }
}