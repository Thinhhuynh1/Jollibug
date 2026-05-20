package vn.fastfood.service;

import vn.fastfood.entity.ChiTietGH;
import vn.fastfood.repository.ChiTietGHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GioHangService {

    @Autowired
    private ChiTietGHRepository chiTietGHRepository;

    public void addToCart(Long maMon, int qty) {

        if (qty < 1) {
            qty = 1;
        }

        Long maGH = 1L; // test tạm - sau này lấy từ session / user hiện tại

        ChiTietGH item = chiTietGHRepository
                .findByMaGHAndMaMon(maGH, maMon)
                .orElse(null);

        if (item != null) {
            item.setSLuong(item.getSLuong() + qty);
        } else {
            item = new ChiTietGH();
            item.setMaGH(maGH);
            item.setMaMon(maMon);
            item.setSLuong(qty);
        }

        chiTietGHRepository.save(item);
    }
}