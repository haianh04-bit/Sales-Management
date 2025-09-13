package com.codegym.services;

import com.codegym.dto.RegisterDTO;
import com.codegym.models.Role;
import com.codegym.models.User;
import com.codegym.models.VerificationToken;
import com.codegym.repositories.PendingUserRepository;
import com.codegym.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private PendingUserRepository pendingUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // Tạo user pending và gửi OTP
    public void createPendingUser(RegisterDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống!");
        }

        VerificationToken pending = pendingUserRepository
                .findByEmail(dto.getEmail())
                .orElse(new VerificationToken());

        pending.setEmail(dto.getEmail());
        pending.setUsername(dto.getUsername());
        pending.setPassword(passwordEncoder.encode(dto.getPassword()));
        pending.setOtp(generateOtp());
        pending.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        pendingUserRepository.save(pending);
        sendOtpToEmail(dto.getEmail(), pending.getOtp());
    }

    // Xác thực OTP
    public boolean verifyOtp(String email, String inputOtp) {
        Optional<VerificationToken> pendingOpt = pendingUserRepository.findByEmail(email);
        if (pendingOpt.isEmpty()) return false;

        VerificationToken pending = pendingOpt.get();

        if (!pending.getOtp().equals(inputOtp)) return false;
        if (pending.getExpiryTime().isBefore(LocalDateTime.now())) return false;

        activateUser(pending);
        return true;
    }

    // Kích hoạt user chính thức từ pending
    private void activateUser(VerificationToken pending) {
        User user = new User();
        user.setEmail(pending.getEmail());
        user.setUsername(pending.getUsername());
        user.setPassword(pending.getPassword());
        user.setEnabled(true);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        pendingUserRepository.delete(pending);
    }

    // Tạo mã OTP ngẫu nhiên 6 chữ số
    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // Gửi email chứa mã OTP
    private void sendOtpToEmail(String to, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("phamhaianhpc10@gmail.com");
            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản của bạn");
            helper.setText(
                    "<p>Xin chào,</p>" +
                            "<p>Mã OTP của bạn là: <b>" + otp + "</b> (hết hạn sau 10 phút).</p>" +
                            "<p>Cảm ơn bạn đã đăng ký!</p>",
                    true
            );

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại!", e);
        }
    }

// Tạo token để reset mật khẩu
    public void createResetToken(String email) {
        VerificationToken token = pendingUserRepository.findByEmail(email)
                .orElse(new VerificationToken());
        token.setEmail(email);
        token.setOtp(generateOtp());
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        pendingUserRepository.save(token);
        sendOtpToEmail(email, token.getOtp());
    }

// Xác thực token reset mật khẩu
    public boolean verifyResetToken(String email, String inputOtp) {
        Optional<VerificationToken> opt = pendingUserRepository.findByEmail(email);
        if (opt.isEmpty()) return false;
        VerificationToken token = opt.get();
        if (!token.getOtp().equals(inputOtp)) return false;
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) return false;
        pendingUserRepository.delete(token); // xoá token sau khi dùng
        return true;
    }

}
