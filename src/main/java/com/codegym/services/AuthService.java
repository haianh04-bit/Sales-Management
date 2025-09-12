package com.codegym.services;

import com.codegym.dto.LoginDTO;
import com.codegym.dto.RegisterDTO;
import com.codegym.models.Role;
import com.codegym.models.User;
import com.codegym.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
