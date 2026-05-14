package vn.fastfood.dto;

public class CartUpdateRequest {
    private long customerId;
    private long maMon;
    private int soLuong;

    public CartUpdateRequest() {
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getMaMon() {
        return maMon;
    }

    public void setMaMon(long maMon) {
        this.maMon = maMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}