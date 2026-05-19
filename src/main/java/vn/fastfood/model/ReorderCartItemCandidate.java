package vn.fastfood.model;

import java.math.BigDecimal;

public class ReorderCartItemCandidate {
    private long maMon;
    private String orderTenMon;
    private String currentTenMon;
    private int requestedQuantity;
    private long availableQuantity;
    private BigDecimal currentPrice;
    private String imageUrl;
    private boolean productExists;
    private boolean available;

    public long getMaMon() {
        return maMon;
    }

    public void setMaMon(long maMon) {
        this.maMon = maMon;
    }

    public String getOrderTenMon() {
        return orderTenMon;
    }

    public void setOrderTenMon(String orderTenMon) {
        this.orderTenMon = orderTenMon;
    }

    public String getCurrentTenMon() {
        return currentTenMon;
    }

    public void setCurrentTenMon(String currentTenMon) {
        this.currentTenMon = currentTenMon;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isProductExists() {
        return productExists;
    }

    public void setProductExists(boolean productExists) {
        this.productExists = productExists;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
