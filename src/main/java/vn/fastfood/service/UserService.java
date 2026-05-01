package vn.fastfood.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.VaiTro;
import vn.fastfood.entity.User;
import vn.fastfood.entity.UserStatus;
import vn.fastfood.repository.RoleRepository;
import vn.fastfood.repository.UserRepository;

@Service
public class UserService {

    private final RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User registerNewUser(User user) {
        List<String> errors = new ArrayList<>();

        // Check for duplicate email
        if (userRepository.findByEmail(user.getEmail()) != null) {
            errors.add("Email đã được đăng ký");
        }

        if (userRepository.findBySdt(user.getSdt()) != null) {
            errors.add("Số điện thoại đã được đăng ký");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(" | ", errors));
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User Login(User user) {
        List<String> errors = new ArrayList<>();
        User x = userRepository.findByEmail(user.getEmail());
        if (x == null) {
            throw new RuntimeException("Email không tồn tại");
        } else {
            if (!passwordEncoder.matches(user.getPassword(), x.getPassword())) {
                throw new RuntimeException("Mật khẩu sai");
            }
        }
        return user;
    }

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public VaiTro getRoleByName(String name) {
        return this.roleRepository.findByTenVT(name);
    }

    public List<User> fetchUserActive() {
        return this.userRepository.findAll();
    }

    public List<User> findByTrangThai(UserStatus trangThai) {
        return this.userRepository.findByTrangThai(trangThai);
    }

    public void deleteUser(long maTK) {
        this.userRepository.deleteById(maTK);
    }

}
