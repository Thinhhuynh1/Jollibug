package vn.fastfood.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.fastfood.entity.ChuongTrinhGiamGia;
import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.ChuongTrinhGiamGiaRepository;

@Service
public class PromotionService {
    private final ChuongTrinhGiamGiaRepository promotionRepository;

    public PromotionService(ChuongTrinhGiamGiaRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void applyPromotions(List<MonAn> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<ChuongTrinhGiamGia> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());
        if (activePromotions == null || activePromotions.isEmpty()) {
            return;
        }

        for (MonAn product : products) {
            double maxDiscountPercent = 0;
            for (ChuongTrinhGiamGia promo : activePromotions) {
                if (isProductInPromo(product, promo)) {
                    if (promo.getPhanTramGiam() > maxDiscountPercent) {
                        maxDiscountPercent = promo.getPhanTramGiam();
                    }
                }
            }

            if (maxDiscountPercent > 0) {
                product.setHasGiamGia(true);
                product.setPhanTramGiam(maxDiscountPercent);
                product.setGiaGiam((long) (product.getGia() * (1 - maxDiscountPercent / 100)));
            }
        }
    }

    private boolean isProductInPromo(MonAn product, ChuongTrinhGiamGia promo) {
        if (promo == null || promo.getPhamViApDung() == null) {
            return false;
        }
        if ("ALL".equals(promo.getPhamViApDung())) {
            return true;
        }
        if ("CATEGORY".equals(promo.getPhamViApDung())) {
            return product.getDanhMuc() != null && promo.getMaDM() != null
                    && product.getDanhMuc().getMaDM() == promo.getMaDM();
        }
        if ("ITEM".equals(promo.getPhamViApDung())) {
            List<Long> monAnIds = promo.getDanhSachMonAnIds();
            return monAnIds != null && monAnIds.contains(product.getMaMon());
        }
        return false;
    }
}
