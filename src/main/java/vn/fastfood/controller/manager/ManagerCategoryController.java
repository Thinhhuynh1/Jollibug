package vn.fastfood.controller.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.fastfood.entity.DanhMuc;
import vn.fastfood.repository.DanhMucRepository;

@Controller
public class ManagerCategoryController {
    private final DanhMucRepository danhMucRepository;

    ManagerCategoryController(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @GetMapping("/manager/categories")
    public String getCategoriesPage(Model model) {
        List<DanhMuc> listDanhMuc = this.danhMucRepository.findListDanhMuc();
        Map<Long, Long> soLuongMonMap = new HashMap<>();

        for (DanhMuc danhMuc : listDanhMuc) {
            soLuongMonMap.put(danhMuc.getMaDM(), this.danhMucRepository.countMonAn(danhMuc.getMaDM()));
        }

        model.addAttribute("listDanhMuc", listDanhMuc);
        model.addAttribute("soLuongMonMap", soLuongMonMap);
        return "manager/categories/show";
    }

    @GetMapping("/manager/categories/create")
    public String getCategoriesAddPage() {
        return "manager/categories/create";
    }

    @PostMapping("/manager/categories/create")
    public String postCreate(@RequestParam("tenDM") String tenDM,
            @RequestParam("moTa") String moTa,
            @RequestParam("available") boolean available) {
        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setTenDM(tenDM);
        danhMuc.setAvailable(available);
        danhMuc.setMoTa(moTa);
        this.danhMucRepository.save(danhMuc);
        return "redirect:/manager/categories";
    }

    @GetMapping("/manager/categories/detail")
    public String getCategoriesDetailPage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        model.addAttribute("danhMuc", this.danhMucRepository.findDanhMuc(categoryID));
        model.addAttribute("soLuongMon", this.danhMucRepository.countMonAn(categoryID));
        return "manager/categories/detail";
    }

    @GetMapping("/manager/categories/update")
    public String getCategoriesUpdatePage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        if (categoryID == null) {
            return "redirect:/manager/categories";
        }
        DanhMuc danhMuc = this.danhMucRepository.findById(categoryID).orElse(null);
        if (danhMuc == null) {
            return "redirect:/manager/categories";
        }
        model.addAttribute("danhMuc", danhMuc);
        return "manager/categories/update";
    }

    @PostMapping("/manager/categories/update")
    public String postCategoriesUpdate(
            @RequestParam("categoryID") Long categoryID,
            @RequestParam("tenDM") String tenDM,
            @RequestParam("moTa") String moTa,
            @RequestParam("available") boolean available) {
        DanhMuc danhMuc = this.danhMucRepository.findDanhMuc(categoryID);

        danhMuc.setTenDM(tenDM);
        danhMuc.setMoTa(moTa);
        danhMuc.setAvailable(available);
        this.danhMucRepository.save(danhMuc);
        return "redirect:/manager/categories";
    }

    @GetMapping("/manager/categories/delete")
    public String getCategoriesDeletePage(@RequestParam(value = "categoryID", required = false) Long categoryID,
            Model model) {
        model.addAttribute("danhMuc", this.danhMucRepository.findDanhMuc(categoryID));

        return "manager/categories/delete";
    }

    @PostMapping("/manager/categories/delete")
    public String postDelete(@RequestParam("categoryID") Long categoryID) {
        DanhMuc danhMuc = this.danhMucRepository.findDanhMuc(categoryID);
        this.danhMucRepository.delete(danhMuc);
        return "redirect:/manager/categories";
    }

}
