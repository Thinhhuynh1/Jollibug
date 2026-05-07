package vn.fastfood.model;

public class CartItem {
    private long maGH;
    private long maMon;
    private int soLuong;

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

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}