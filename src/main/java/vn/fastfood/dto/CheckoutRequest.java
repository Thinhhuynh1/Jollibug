package vn.fastfood.dto;

public class CheckoutRequest {
    private long customerId;
    private Long maDC;
    private String discountCode;
    private String maPT;
    private String ghiChu;

    public CheckoutRequest() {
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Long getMaDC() {
        return maDC;
    }

    public void setMaDC(Long maDC) {
        this.maDC = maDC;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getMaPT() {
        return maPT;
    }

    public void setMaPT(String maPT) {
        this.maPT = maPT;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}