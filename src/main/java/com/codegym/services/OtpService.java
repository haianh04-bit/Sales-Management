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

    public void createPendingUser(RegisterDTO dto) {
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

    public boolean verifyOtp(String email, String inputOtp) {
        Optional<VerificationToken> pendingOpt = pendingUserRepository.findByEmail(email);
        if (pendingOpt.isEmpty()) return false;

        VerificationToken pending = pendingOpt.get();

        // check OTP
        if (!pending.getOtp().equals(inputOtp)) return false;

        // check expiry
        if (pending.getExpiryTime().isBefore(LocalDateTime.now())) return false;

        // Tạo user chính thức
        User user = new User();
        user.setEmail(pending.getEmail());
        user.setUsername(pending.getUsername());
        user.setPassword(pending.getPassword());
        user.setEnabled(true); // chỉ enable sau khi verify
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        // xoá pending
        pendingUserRepository.delete(pending);

        return true;
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

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
}
