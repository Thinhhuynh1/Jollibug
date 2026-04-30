package vn.fastfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.User;
import vn.fastfood.entity.UserStatus;
import vn.fastfood.repository.UserRepository;
import vn.fastfood.repository.VaiTroRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user){
        if (userRepository.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Email đã được đăng ký");
        }

        if (userRepository.findBySdt(user.getSdt()) != null){
            throw new RuntimeException("Số điện thoại đã được đăng ký");
        }
        
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // Set vi trò default là khách hàng (ROLE_CLIENT - MaVT = 2)
        user.setVaiTro(vaiTroRepository.findByMaVT("2"));
        
        // Set trạng thái default là ACTIVE
        user.setTrangThai(UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    public User login(String email, String password){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email không tồn tại");      
        }
        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Mật khẩu sai");
        }  
        return user;
    }
}
