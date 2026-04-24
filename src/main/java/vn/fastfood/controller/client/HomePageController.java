package vn.fastfood.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomePageController {
    // home,about,contact

    @GetMapping("/")
    public String getHomePage() {
        return "client/homepage";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "client/about";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "client/contact";
    }

    @GetMapping("/menu")
    public String getMenuPage() {
        return "client/menu";
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        // TODO: Thêm logic để lấy thông tin sản phẩm từ database
        // Ví dụ: Product product = productService.findById(id);
        // model.addAttribute("product", product);
        
        // Tạm thời chỉ trả về view
        return "client/product";
    }

}
