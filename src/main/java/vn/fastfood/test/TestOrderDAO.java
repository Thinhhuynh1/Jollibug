package vn.fastfood.test;

import vn.fastfood.dao.DonHangDAO;
import vn.fastfood.model.DonHang;
import java.util.List;

public class TestOrderDAO {
    public static void main(String[] args) {
        DonHangDAO orderDAO = new DonHangDAO();
        long customerId = 1;
        List<DonHang> orders = orderDAO.getOrdersByCustomerId(customerId);
        if (orders.isEmpty())
            System.out.println("Khong tim thay don hang nao cua khach hang");
        else {
            System.out.println("Danh sach don hang: ");
            for (DonHang order : orders)
                System.out.println(order);
        }
    }
}
