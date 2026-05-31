package vn.fastfood.dto;

public class ProductRequest {
    private String tenMon;
    private Long maDM;
    private Long gia;
    private Long soLuongTon;
    private String moTa;
    private String donVi;
    private Boolean available;

    public ProductRequest() {
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public Long getMaDM() {
        return maDM;
    }

    public void setMaDM(Long maDM) {
        this.maDM = maDM;
    }

    public Long getGia() {
        return gia;
    }

    public void setGia(Long gia) {
        this.gia = gia;
    }

    public Long getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Long soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
