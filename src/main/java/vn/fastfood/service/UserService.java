package vn.fastfood.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.User;
import vn.fastfood.entity.VaiTro;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.repository.VaiTroRepository;

@Service
public class UserService {

    private final VaiTroRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserService(VaiTroRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User registerNewUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email đã được đăng ký");
        }

        if (userRepository.findBySdt(user.getSdt()) != null) {
            throw new RuntimeException("Số điện thoại đã được đăng ký");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Set vai trò default là khách hàng (ROLE_CLIENT)
        user.setVaiTro(vaiTroRepository.findByTenVT(""));

        // Set trạng thái default là ACTIVE
        user.setTrangThai("ACTIVE");

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email không tồn tại");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu sai");
        }
        return user;
    }

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public VaiTro getRoleByName(String name) {
        return this.vaiTroRepository.findByTenVT(name);
    }

    public List<User> getUserActive() {
        return this.userRepository.findAll();
    }

    public List<User> findByTrangThai(String trangThai) {
        return this.userRepository.findByTrangThai(trangThai);
    }

    public void deleteUser(long maTK) {
        this.userRepository.deleteById(maTK);
    }

    public User getUserByMaTK(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public void resetPassword(jakarta.servlet.http.HttpSession session, String currentPassword, String newPassword) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Vui lòng đăng nhập lại");
        }
        User dbUser = userRepository.findByMaTK(user.getMaTK());
        if (dbUser == null) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        if (!passwordEncoder.matches(currentPassword, dbUser.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        dbUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(dbUser);
        session.removeAttribute("user");
    }

}
