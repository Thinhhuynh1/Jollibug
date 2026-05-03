package vn.fastfood.dto;

import vn.fastfood.model.OrderItem;
import vn.fastfood.model.Order;
import java.util.List;

public class OrderDetailResponse {
    private Order dh;
    private List<OrderItem> ctDH;

    public OrderDetailResponse() {}

    public OrderDetailResponse(Order dh, List<OrderItem> ctDH) {
        this.dh = dh;
        this.ctDH = ctDH;
    }

    public Order getDonHang() {
        return dh;
    }

    public void setDonHang(Order dh) {
        this.dh = dh;
    }

    public List<OrderItem> getChiTietDH() {
        return ctDH;
    }

    public void setChiTietDH(List<OrderItem> ctDH) {
        this.ctDH = ctDH;
    }
}
