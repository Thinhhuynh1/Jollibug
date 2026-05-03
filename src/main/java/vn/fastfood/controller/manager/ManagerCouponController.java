package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerCouponController {
    @GetMapping("/manager/coupons")
    public String getCouponsPage() {
        return "manager/coupons/show";
    }

    @GetMapping("/manager/coupons/create")
    public String getCouponsCreatePage() {
        return "manager/coupons/create";
    }

    @GetMapping("/manager/coupons/detail")
    public String getCouponsDetailPage() {
        return "manager/coupons/detail";
    }

    @GetMapping("/manager/coupons/update")
    public String getCouponsUpdatePage() {
        return "manager/coupons/update";
    }

    @GetMapping("/manager/coupons/delete")
    public String getCouponsDeletePage() {
        return "manager/coupons/delete";
    }
}
