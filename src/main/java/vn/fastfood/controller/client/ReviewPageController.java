package vn.fastfood.controller.client;



import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import jakarta.servlet.http.HttpSession;

import vn.fastfood.dto.ReviewBatchRequest;

import vn.fastfood.entity.User;

import vn.fastfood.repository.DonHangRepository;

import vn.fastfood.service.ReviewService;



@Controller

public class ReviewPageController {



    private final ReviewService reviewService;

    private final DonHangRepository donHangRepository;



    public ReviewPageController(ReviewService reviewService,

            DonHangRepository donHangRepository) {

        this.reviewService = reviewService;

        this.donHangRepository = donHangRepository;

    }



    @GetMapping("/orders/reviews")

    public String listReviews(@RequestParam(value = "orderId", required = false) Long orderId,

            Model model, HttpSession session) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        model.addAttribute("orderId", orderId);

        model.addAttribute("order", orderId != null ? donHangRepository.findById(orderId).orElse(null) : null);

        model.addAttribute("pendingOrders",

                reviewService.listDeliveredOrdersWithReviewableItems(user.getMaTK()));

        model.addAttribute("reviews", reviewService.listByCustomer(user.getMaTK(), orderId));

        return "client/orders/reviews";

    }



    @GetMapping("/orders/reviews/create")

    public String createReviewForm(@RequestParam(value = "orderId", required = false) Long orderId,

            Model model, HttpSession session) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        model.addAttribute("orderId", orderId);

        model.addAttribute("order", orderId != null ? donHangRepository.findById(orderId).orElse(null) : null);

        model.addAttribute("orderItems",

                orderId != null ? reviewService.listReviewableItems(orderId, user.getMaTK()) : null);

        model.addAttribute("deliveredOrders",

                reviewService.listDeliveredOrdersWithReviewableItems(user.getMaTK()));

        return "client/orders/reviewsCreate";

    }



    @PostMapping("/orders/reviews/create")

    public String submitReview(@ModelAttribute ReviewBatchRequest request,

            HttpSession session,

            RedirectAttributes redirectAttributes) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }



        long orderId = request.getOrderId();

        try {

            int count = reviewService.addReviewsBatch(orderId, user.getMaTK(), request.getItems());

            redirectAttributes.addFlashAttribute("message",

                    "Đã gửi " + count + " đánh giá cho đơn #" + orderId + ".");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());

            return "redirect:/orders/reviews/create?orderId=" + orderId;

        }

        return "redirect:/orders/reviews?orderId=" + orderId;

    }



    @GetMapping("/orders/reviews/view")

    public String viewReview(@RequestParam("reviewId") long reviewId, Model model, HttpSession session) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        return reviewService.findReview(reviewId, user.getMaTK())

                .map(review -> {

                    model.addAttribute("review", review);

                    model.addAttribute("canEdit", reviewService.canEditReview(review));

                    model.addAttribute("editDeadlineDisplay",

                            reviewService.getEditDeadline(review).format(

                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                    return "client/orders/reviewsView";

                })

                .orElse("redirect:/orders/reviews");

    }



    @GetMapping("/orders/reviews/update")

    public String updateReviewForm(@RequestParam("reviewId") long reviewId,

            Model model,

            HttpSession session,

            RedirectAttributes redirectAttributes) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        return reviewService.findReview(reviewId, user.getMaTK())

                .map(review -> {

                    if (!reviewService.canEditReview(review)) {

                        redirectAttributes.addFlashAttribute("error",

                                "Chỉ có thể sửa đánh giá trong vòng "

                                        + ReviewService.REVIEW_EDIT_MONTHS

                                        + " tháng kể từ ngày đánh giá.");

                        return "redirect:/orders/reviews/view?reviewId=" + reviewId;

                    }

                    model.addAttribute("review", review);

                    model.addAttribute("editDeadlineDisplay",

                            reviewService.getEditDeadline(review).format(

                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                    return "client/orders/reviewsUpdate";

                })

                .orElse("redirect:/orders/reviews");

    }



    @PostMapping("/orders/reviews/update")

    public String submitUpdateReview(@RequestParam("reviewId") long reviewId,

            @RequestParam("sao") int sao,

            @RequestParam("noiDung") String noiDung,

            HttpSession session,

            RedirectAttributes redirectAttributes) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        try {

            reviewService.updateReview(reviewId, user.getMaTK(), sao, noiDung);

            redirectAttributes.addFlashAttribute("message", "Đã cập nhật đánh giá.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());

            return "redirect:/orders/reviews/view?reviewId=" + reviewId;

        }

        return "redirect:/orders/reviews";

    }



    @GetMapping("/orders/reviews/delete")

    public String deleteReviewForm(@RequestParam("reviewId") long reviewId, Model model, HttpSession session) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        return reviewService.findReview(reviewId, user.getMaTK())

                .map(review -> {

                    model.addAttribute("review", review);

                    return "client/orders/reviewsDelete";

                })

                .orElse("redirect:/orders/reviews");

    }



    @PostMapping("/orders/reviews/delete")

    public String submitDeleteReview(@RequestParam("reviewId") long reviewId,

            HttpSession session,

            RedirectAttributes redirectAttributes) {

        User user = requireUser(session);

        if (user == null) {

            return "redirect:/login";

        }

        try {

            reviewService.deleteReview(reviewId, user.getMaTK());

            redirectAttributes.addFlashAttribute("message", "Đã xóa đánh giá.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());

        }

        return "redirect:/orders/reviews";

    }



    private User requireUser(HttpSession session) {

        Object user = session.getAttribute("user");

        return user instanceof User ? (User) user : null;

    }

}

