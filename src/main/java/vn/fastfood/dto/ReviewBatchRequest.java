package vn.fastfood.dto;

import java.util.ArrayList;
import java.util.List;

public class ReviewBatchRequest {

    private long orderId;
    private List<ReviewRequest> items = new ArrayList<>();

    public ReviewBatchRequest() {
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<ReviewRequest> getItems() {
        return items;
    }

    public void setItems(List<ReviewRequest> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
