package vn.fastfood.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.fastfood.entity.User;
import vn.fastfood.entity.VaiTro;
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

    @Autowired
    private DataSource dataSource;

    private boolean callFunction(String sql, String value) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, value);
            statement.execute();
            return statement.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy function", e);
        }
    }

    private boolean emailExists(String email) {
        return callFunction("{? = call FUNC_EMAIL_EXISTS(?)}", email);
    }

    private boolean phoneExists(String phone) {
        if (phone == null) {
            return false;
        }
        return callFunction("{? = call FUNC_SDT_EXISTS(?)}", phone);
    }

    private void updatePasswordById(Long userId, String encodedPassword) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_UPDATE_USER_PASSWORD(?, ?)}")) {
            statement.setLong(1, userId);
            statement.setString(2, encodedPassword);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailClean(email);
    }

    public User getUserByMaTK(Long id) {
        return this.userRepository.findById(id).orElse(null);
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

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUser(long maTK) {
        this.userRepository.deleteById(maTK);
    }

    public User registerNewUser(User user) {
        String email = user.getEmail();
        String phone = user.getSdt();

        if (emailExists(email)) {
            throw new RuntimeException("Email đã được đăng ký");
        }

        if (phoneExists(phone)) {
            throw new RuntimeException("Số điện thoại đã được đăng ký");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{call PROC_REGISTER_USER(?, ?, ?, ?)}")) {
            statement.setString(1, encodedPassword);
            statement.setString(2, user.getHoTen());
            statement.setString(3, phone);
            statement.setString(4, email);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể chạy procedure", e);
        }

        return userRepository.findByEmailClean(email);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmailClean(email);
        if (user == null) {
            throw new RuntimeException("Email không tồn tại");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu sai");
        }

        return user;
    }

    public String resetPassword(HttpSession session, String currentPass, String newPass) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(sessionUser.getMaTK())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        if (!passwordEncoder.matches(currentPass, user.getPassword())) {
            throw new RuntimeException("Mật khẩu sai");
        }

        String encodedPassword = passwordEncoder.encode(newPass);
        updatePasswordById(user.getMaTK(), encodedPassword);

        User refreshedUser = userRepository.findById(user.getMaTK())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        session.setAttribute("user", refreshedUser);

        return "redirect:/login";
    }

    public String changePassword(HttpSession session, String newPass) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/forgot-password";
        }

        String encodedPassword = passwordEncoder.encode(newPass);
        updatePasswordById(user.getMaTK(), encodedPassword);

        User refreshedUser = userRepository.findById(user.getMaTK()).orElse(user);
        session.setAttribute("user", refreshedUser);
        return "redirect:/login";
    }

    public void changePasswordByEmail(String email, String newPass) {
        User user = userRepository.findByEmailClean(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy tài khoản cần đặt lại mật khẩu");
        }

        String encodedPassword = passwordEncoder.encode(newPass);
        updatePasswordById(user.getMaTK(), encodedPassword);
    }
}
