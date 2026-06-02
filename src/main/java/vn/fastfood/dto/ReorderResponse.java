package vn.fastfood.dto;

import vn.fastfood.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ReorderResponse {
    private boolean success;
    private String message;
    private List<CartItem> items = new ArrayList<>();
    private List<String> skippedItems = new ArrayList<>();

    public ReorderResponse() {
    }

    public ReorderResponse(boolean success, String message, List<CartItem> items, List<String> skippedItems) {
        this.success = success;
        this.message = message;
        this.items = items == null ? new ArrayList<>() : items;
        this.skippedItems = skippedItems == null ? new ArrayList<>() : skippedItems;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    public List<String> getSkippedItems() {
        return skippedItems;
    }

    public void setSkippedItems(List<String> skippedItems) {
        this.skippedItems = skippedItems == null ? new ArrayList<>() : skippedItems;
    }
}
