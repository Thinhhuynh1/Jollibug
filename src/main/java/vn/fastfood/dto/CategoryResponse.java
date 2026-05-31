package vn.fastfood.dto;

import vn.fastfood.entity.DanhMuc;

public class CategoryResponse {
    private long maDM;
    private String tenDM;
    private String moTa;
    private boolean available;
    private long soMon;

    public static CategoryResponse from(DanhMuc danhMuc, long soMon) {
        CategoryResponse response = new CategoryResponse();
        response.maDM = danhMuc.getMaDM();
        response.tenDM = danhMuc.getTenDM();
        response.moTa = danhMuc.getMoTa();
        response.available = danhMuc.isAvailable();
        response.soMon = soMon;
        return response;
    }

    public long getMaDM() {
        return maDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public String getMoTa() {
        return moTa;
    }

    public boolean isAvailable() {
        return available;
    }

    public long getSoMon() {
        return soMon;
    }
}
