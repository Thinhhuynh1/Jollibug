package vn.fastfood.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.User;
import vn.fastfood.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user){
        List<String> errors = new ArrayList<>();

        // Check for duplicate username
        if (userRepository.findByUsername(user.getUsername()) != null){
            errors.add("Tên đăng nhập đã được sử dụng");
        }

        // Check for duplicate email
        if (userRepository.findByEmail(user.getEmail()) != null){
            errors.add("Email đã được đăng ký");
        }

        if (userRepository.findBySdt(user.getSdt()) != null){
            errors.add("Số điện thoại đã được đăng ký");
        }
        
        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(" | ", errors));
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User Login(User user){
        List<String> errors = new ArrayList<>();
        User x = userRepository.findByEmail(user.getEmail());
        if (x == null) {
            throw new RuntimeException("Email không tồn tại");      
        }
        else {
            if (!passwordEncoder.matches(user.getPassword(), x.getPassword())){
                throw new RuntimeException("Mật khẩu sai");
            }
        }  
        return user;
    }
}
