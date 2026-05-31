package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;
import vn.fastfood.repository.ChiTietDHRepository;
import vn.fastfood.repository.DonHangRepository;
import vn.fastfood.repository.MonAnRepository;
import vn.fastfood.service.ReviewService;

@Controller
public class ReviewPageController {

    private final ReviewService reviewService;
    private final DonHangRepository donHangRepository;
    private final ChiTietDHRepository chiTietDHRepository;
    private final MonAnRepository monAnRepository;

    public ReviewPageController(ReviewService reviewService,
            DonHangRepository donHangRepository,
            ChiTietDHRepository chiTietDHRepository,
            MonAnRepository monAnRepository) {
        this.reviewService = reviewService;
        this.donHangRepository = donHangRepository;
        this.chiTietDHRepository = chiTietDHRepository;
        this.monAnRepository = monAnRepository;
    }

    @GetMapping("/orders/reviews")
    public String listReviews(Model model, HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("reviews", reviewService.listByCustomer(user.getMaTK()));
        return "client/orders/reviews";
    }

    @GetMapping("/orders/reviews/create")
    public String createReviewForm(@RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "maMon", required = false) Long maMon,
            Model model, HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("orderId", orderId);
        model.addAttribute("maMon", maMon);
        model.addAttribute("monAn", maMon != null ? monAnRepository.findProduct(maMon) : null);
        model.addAttribute("order", orderId != null ? donHangRepository.findById(orderId).orElse(null) : null);
        model.addAttribute("orderItems", orderId != null ? chiTietDHRepository.findByMaDH(orderId) : null);
        model.addAttribute("deliveredOrders",
                donHangRepository.findByUser_MaTKAndTrangThaiOrderByNgayDatDesc(user.getMaTK(), "DELIVERED"));
        return "client/orders/reviewsCreate";
    }

    @PostMapping("/orders/reviews/create")
    public String submitReview(@RequestParam("orderId") long orderId,
            @RequestParam("maMon") long maMon,
            @RequestParam("sao") int sao,
            @RequestParam("noiDung") String noiDung,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = requireUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        try {
            reviewService.addReview(orderId, user.getMaTK(), maMon, sao, noiDung);
            redirectAttributes.addFlashAttribute("message", "Đánh giá thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/orders/reviews/create?orderId=" + orderId + "&maMon=" + maMon;
        }
        return "redirect:/orders/reviews";
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
                    return "client/orders/reviewsView";
                })
                .orElse("redirect:/orders/reviews");
    }

    @GetMapping("/orders/reviews/update")
    public String updateReviewForm(@RequestParam("reviewId") long reviewId, Model model, HttpSession session) {
        User user = requireUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        return reviewService.findReview(reviewId, user.getMaTK())
                .map(review -> {
                    model.addAttribute("review", review);
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
