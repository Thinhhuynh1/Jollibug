package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    @GetMapping("/reset-password")
    public String getResetPasswordPage() {
        return "client/reset-password";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "client/profile";
    }

    @GetMapping("/address")
    public String getAddressPage() {
        return "client/address/show";
    }

    @GetMapping("/address/create")
    public String getAddressCreate() {
        return "client/address/create";
    }

    @GetMapping("/address/update")
    public String getAddressUpdate() {
        return "client/address/update";
    }

    @GetMapping("/address/delete")
    public String getAddressDelete() {
        return "client/address/delete";
    }

    @GetMapping("/orders")
    public String getOrderPage() {
        return "client/orders/show";
    }

    @GetMapping("/orders/pending")
    public String getOrderPending() {
        return "client/orders/pending";
    }

    @GetMapping("/orders/confirmed")
    public String getOrderConfirmed() {
        return "client/orders/confirmed";
    }

    @GetMapping("/orders/shipping")
    public String getOrderShipping() {
        return "client/orders/shipping";
    }

    @GetMapping("/orders/delivered")
    public String getOrderDelivered() {
        return "client/orders/delivered";
    }

    @GetMapping("/orders/cancelled")
    public String getOrderCancelled() {
        return "client/orders/cancelled";
    }

    @GetMapping("/orders/cancel")
    public String getOrderCancel() {
        return "client/orders/cancel";
    }

    @GetMapping("/orders/detail")
    public String getOrderDetail() {
        return "client/orders/detail";
    }

    @GetMapping("/orders/reviews")
    public String getOrderReviews() {
        return "client/orders/reviews";
    }

    @GetMapping("/orders/reviews/create")
    public String getReviewsCreate() {
        return "client/orders/reviewsCreate";
    }

    @GetMapping("/orders/reviews/view")
    public String getReviewsView() {
        return "client/orders/reviewsView";
    }

    @GetMapping("/orders/reviews/update")
    public String getReviewsUpdate() {
        return "client/orders/reviewsUpdate";
    }

    @GetMapping("/orders/reviews/delete")
    public String getReviewsDelete() {
        return "client/orders/reviewsDelete";
    }
}
