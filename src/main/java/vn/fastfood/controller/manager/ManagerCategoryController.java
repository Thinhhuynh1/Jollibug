package vn.fastfood.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerCategoryController {
    @GetMapping("/manager/categories")
    public String getCategoriesPage() {
        return "manager/categories/show";
    }

    @GetMapping("/manager/categories/create")
    public String getCategoriesAddPage() {
        return "manager/categories/create";
    }

    @GetMapping("/manager/categories/detail")
    public String getCategoriesDetailPage() {
        return "manager/categories/detail";
    }

    @GetMapping("/manager/categories/update")
    public String getCategoriesUpdatePage() {
        return "manager/categories/update";
    }

    @GetMapping("/manager/categories/delete")
    public String getCategoriesDeletePage() {
        return "manager/categories/delete";
    }
}
