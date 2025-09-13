package com.codegym.controller;

import com.codegym.dto.LoginDTO;
import com.codegym.dto.RegisterDTO;
import com.codegym.models.Role;
import com.codegym.models.User;
import com.codegym.models.VerificationToken;
import com.codegym.repositories.PendingUserRepository;
import com.codegym.repositories.UserRepository;
import com.codegym.services.AuthService;
import com.codegym.services.OtpService;
import com.codegym.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, OtpService otpService, UserRepository userRepository) {
        this.authService = authService;
        this.otpService = otpService;
        this.userRepository = userRepository;
    }

    // Form đăng nhập
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    // Form đăng ký
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    // Xử lý đăng ký và gửi OTP
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        otpService.createPendingUser(dto);
        model.addAttribute("email", dto.getEmail());
        model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn!");
        return "auth/verify-otp";
    }

    // Form xác thực OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "OTP không hợp lệ hoặc đã hết hạn!");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
        model.addAttribute("success", "Xác thực thành công! Bạn có thể đăng nhập.");
        return "redirect:/login";
    }

    // Form đổi mật khẩu
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "auth/change-password";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        String email = authentication.getName();
        User user = authService.login(new LoginDTO(email, oldPassword)); // validate login
        try {
            authService.changePassword(user.getId(), oldPassword, newPassword);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/change-password";
        }
        return "redirect:/user/profile?passwordChanged";
    }

    // Form quên mật khẩu
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Xử lý gửi OTP đặt lại mật khẩu
    @PostMapping("/forgot-password")
    public String sendResetOtp(@RequestParam("email") String email, Model model) {
        if (!userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "auth/forgot-password";
        }

        otpService.createResetToken(email);

        // Redirect sang reset-password, kèm email để tự động điền vào form
        return "redirect:/reset-password?email=" + email;
    }


    // Form nhập OTP và mật khẩu mới (GET)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "email", required = false) String email,
                                        Model model) {
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    // Xử lý đặt lại mật khẩu (POST)
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("otp") String otp,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        if (!otpService.verifyResetToken(email, otp)) {
            model.addAttribute("error", "OTP không hợp lệ hoặc đã hết hạn!");
            model.addAttribute("email", email);
            return "auth/reset-password";
        }
        authService.resetPassword(email, newPassword);
        return "redirect:/login?passwordReset";
    }

}
