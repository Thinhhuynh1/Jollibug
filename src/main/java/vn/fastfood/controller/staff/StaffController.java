package vn.fastfood.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class StaffController {

    @GetMapping("/staff")
    public String getOrders() {
        return "redirect:/staff/orders/confirmed";
    }

    @GetMapping("/staff/orders/confirmed")
    public String getConfirmedOrdersPage(Model model) {
        model.addAttribute("orderTab", "confirmed");
        return "/staff/orders";
    }

    @GetMapping("/staff/orders/unconfirmed")
    public String getUnconfirmedOrdersPage(Model model) {
        model.addAttribute("orderTab", "unconfirmed");
        return "/staff/orders";
    }

    @GetMapping("/staff/orders/detail")
    public String getOrderDetailPage() {
        return "/staff/orders/detail";
    }

    @GetMapping("/staff/orders/update-status")
    public String getOrderUpdateStatusPage() {
        return "/staff/orders/update-status";
    }

    @GetMapping("/staff/orders/confirm")
    public String getOrderConfirmPage() {
        return "/staff/orders/confirm";
    }

    @GetMapping("/staff/support")
    public String getSupportPage() {
        return "/staff/support";
    }

    @PostMapping("/staff/support/chat/send")
    public String sendSupportChat(@RequestParam("message") String message) {
        return "redirect:/staff/support?tab=chat";
    }

    @PostMapping("/staff/support/complaint/reply")
    public String replyComplaint(@RequestParam("reply") String reply) {
        return "redirect:/staff/support?tab=complaint";
    }

    @PostMapping("/staff/support/review/reply")
    public String replyReview(@RequestParam("reply") String reply) {
        return "redirect:/staff/support?tab=review";
    }

    @GetMapping("/staff/clients")
    public String getClientsPage() {
        return "/staff/clients/show";
    }

    @GetMapping("/staff/clients/detail")
    public String getClientDetailPage() {
        return "/staff/clients/detail";
    }

}
