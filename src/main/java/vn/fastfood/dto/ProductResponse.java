package vn.fastfood.dto;

import vn.fastfood.entity.MonAn;

public class ProductResponse {
    private long maMon;
    private String tenMon;
    private String moTa;
    private long gia;
    private long soLuongTon;
    private long soLuongDaBan;
    private String img;
    private boolean available;
    private long maDM;
    private String tenDM;

    public static ProductResponse from(MonAn monAn) {
        ProductResponse response = new ProductResponse();
        response.maMon = monAn.getMaMon();
        response.tenMon = monAn.getTenMon();
        response.moTa = monAn.getMoTa();
        response.gia = monAn.getGia();
        response.soLuongTon = monAn.getSoLuongTon();
        response.soLuongDaBan = monAn.getSoLuongDaBan();
        response.img = monAn.getImg();
        response.available = monAn.isAvailable();
        if (monAn.getDanhMuc() != null) {
            response.maDM = monAn.getDanhMuc().getMaDM();
            response.tenDM = monAn.getDanhMuc().getTenDM();
        }
        return response;
    }

    public long getMaMon() {
        return maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public String getMoTa() {
        return moTa;
    }

    public long getGia() {
        return gia;
    }

    public long getSoLuongTon() {
        return soLuongTon;
    }

    public long getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public String getImg() {
        return img;
    }

    public boolean isAvailable() {
        return available;
    }

    public long getMaDM() {
        return maDM;
    }

    public String getTenDM() {
        return tenDM;
    }
}
