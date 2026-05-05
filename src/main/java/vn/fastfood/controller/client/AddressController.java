package vn.fastfood.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.DiaChi;
import vn.fastfood.entity.User;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.service.AddressService;

@Controller
public class AddressController {
    private final AddressService addressService;
    private final UserRepository userRepository;

    AddressController(AddressService addressService, UserRepository userRepository) {
        this.addressService = addressService;
        this.userRepository = userRepository;
    }

    @GetMapping("/address")
    public String getAddressPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login";
        }

        Object maTKObj = session.getAttribute("userId");
        if (!(maTKObj instanceof Number maTKNumber)) {
            return "redirect:/login";
        }

        long maTK = maTKNumber.longValue();
        User user = this.userRepository.findByMaTK(maTK);
        if (user == null) {
            return "redirect:/login";
        }

        List<DiaChi> listAddress = user.getDiaChi();
        model.addAttribute("listAddress", listAddress);
        return "client/address/show";
    }

    @GetMapping("/address/create")
    public String getAddressCreate() {
        return "/client/address/create";
    }

    @PostMapping("/address/create")
    public String postAddressCreate(Model model,
            @RequestParam("tenNguoiNhan") String ten,
            @RequestParam("sdtNguoiNhan") String sdt,
            @RequestParam("tenDiaChi") String tenDiaChi,
            @RequestParam("tinhThanh") String tinhThanh,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("phuongXa") String phuongXa,
            @RequestParam("diaChiCuThe") String diaChi,
            HttpSession session) {

        DiaChi address = new DiaChi();
        Object maTKObj = session.getAttribute("userId");

        address.setTenNguoiNhan(ten);
        address.setSdtNguoiNhan(sdt);
        address.setTenDiaChi(tenDiaChi);
        address.setTinhThanh(tinhThanh);
        address.setQuanHuyen(quanHuyen);
        address.setPhuongXa(phuongXa);
        address.setDiaChiCuThe(diaChi);

        if (maTKObj instanceof Number maTKNumber) {
            long maTK = maTKNumber.longValue();
            User user = this.userRepository.findByMaTK(maTK);
            if (user == null) {
                return "redirect:/login";
            }

            address.setUser(user);
            if (!this.addressService.hasAddress(maTK)) {
                address.setDefaultAddress(true);
            }
        } else {
            return "redirect:/login";
        }

        this.addressService.saveDiaChi(address);

        return "redirect:/address";
    }

    @GetMapping("/address/update/{maDC}")
    public String getAddressUpdate(Model model,
            @PathVariable long maDC) {
        DiaChi diaChi = this.addressService.findDiaChi(maDC);
        model.addAttribute("address", diaChi);
        return "client/address/update";
    }

    @PostMapping("/address/update/{maDC}")
    public String postAddressUpdate(
            @PathVariable long maDC,
            @RequestParam("tenNguoiNhan") String ten,
            @RequestParam("sdtNguoiNhan") String sdt,
            @RequestParam("tenDiaChi") String tenDiaChi,
            @RequestParam("tinhThanh") String tinhThanh,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("phuongXa") String phuongXa,
            @RequestParam("diaChiCuThe") String diaChi,
            HttpSession session) {

        Object maTKObj = session.getAttribute("userId");
        if (!(maTKObj instanceof Number maTKNumber)) {
            return "redirect:/login";
        }

        long maTK = maTKNumber.longValue();
        User user = this.userRepository.findByMaTK(maTK);
        if (user == null) {
            return "redirect:/login";
        }

        DiaChi address = this.addressService.findDiaChi(maDC);
        if (address == null || address.getUser() == null || address.getUser().getMaTK() != maTK) {
            return "redirect:/address";
        }

        address.setTenNguoiNhan(ten);
        address.setSdtNguoiNhan(sdt);
        address.setTenDiaChi(tenDiaChi);
        address.setTinhThanh(tinhThanh);
        address.setQuanHuyen(quanHuyen);
        address.setPhuongXa(phuongXa);
        address.setDiaChiCuThe(diaChi);

        this.addressService.saveDiaChi(address);

        return "redirect:/address";
    }

    @GetMapping("/address/delete/{maDC}")
    public String getAddressDelete(Model model, @PathVariable long maDC) {
        DiaChi diaChi = this.addressService.findDiaChi(maDC);
        model.addAttribute("address", diaChi);
        return "client/address/delete";
    }

    @PostMapping("/address/delete/{maDC}")
    public String postAddressDelete(@PathVariable long maDC, HttpSession session) {
        Object maTKObj = session.getAttribute("userId");
        if (!(maTKObj instanceof Number maTKNumber)) {
            return "redirect:/login";
        }

        long maTK = maTKNumber.longValue();
        this.addressService.deleteDiaChi(maTK, maDC);
        return "redirect:/address";
    }

    @PostMapping("/address/default/{maDC}")
    public String updateDefault(@PathVariable long maDC, HttpSession session) {
        Object maTKObj = session.getAttribute("userId");
        if (!(maTKObj instanceof Number maTKNumber)) {
            return "redirect:/login";
        }

        long maTK = maTKNumber.longValue();
        this.addressService.setDefaultAddress(maTK, maDC);
        return "redirect:/address";
    }
}
