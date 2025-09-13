package com.codegym.services;

import com.codegym.dto.LoginDTO;
import com.codegym.dto.RegisterDTO;
import com.codegym.dto.UserDTO;
import com.codegym.models.Role;
import com.codegym.models.User;
import com.codegym.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Đăng ký người dùng mới (encode mật khẩu)
     */
    public User register(RegisterDTO dto) {
        // kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(false); // mặc định false, sẽ bật sau khi verify OTP

        return user;
    }

    /**
     * Đăng nhập
     */
    public User login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null) {
            throw new RuntimeException("Email không tồn tại!");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản chưa được xác thực!");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu!");
        }

        return user;
    }

    // 2. Đổi mật khẩu
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 3. Reset mật khẩu (quên password) -> đặt password mới (thông qua email OTP chẳng hạn)
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
