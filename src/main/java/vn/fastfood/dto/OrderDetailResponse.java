package vn.fastfood.dto;

import vn.fastfood.model.Order;
import vn.fastfood.model.OrderItem;

import java.util.List;

public class OrderDetailResponse {
    private Order order;
    private List<OrderItem> orderItems;
    private List<ReviewResponse> reviews;

    public OrderDetailResponse() {
    }

    public OrderDetailResponse(Order order, List<OrderItem> orderItems) {
        this.order = order;
        this.orderItems = orderItems;
    }

    public OrderDetailResponse(Order order, List<OrderItem> orderItems, List<ReviewResponse> reviews) {
        this.order = order;
        this.orderItems = orderItems;
        this.reviews = reviews;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
}