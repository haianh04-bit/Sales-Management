package com.codegym.controller;

import com.codegym.dto.LoginDTO;
import com.codegym.dto.RegisterDTO;
import com.codegym.models.Role;
import com.codegym.models.User;
import com.codegym.models.VerificationToken;
import com.codegym.repositories.PendingUserRepository;
import com.codegym.services.AuthService;
import com.codegym.services.OtpService;
import com.codegym.services.UserService;
import jakarta.validation.Valid;
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
    private final PendingUserRepository pendingUserRepository;
    private final UserService userService;

    public AuthController(AuthService authService, OtpService otpService, PendingUserRepository pendingUserRepository, UserService userService) {
        this.authService = authService;
        this.otpService = otpService;
        this.pendingUserRepository = pendingUserRepository;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

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

    @GetMapping("/verify-otp")
    public String showVerifyOtp(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        Optional<VerificationToken> optional = pendingUserRepository.findByEmail(email);
        if (optional.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại hoặc chưa đăng ký!");
            return "auth/verify-otp";
        }
        VerificationToken token = optional.get();
        if (!token.getOtp().equals(otp)) {
            model.addAttribute("error", "Mã OTP không đúng!");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã OTP đã hết hạn!");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
        User user = new User();
        user.setUsername(token.getUsername());
        user.setEmail(token.getEmail());
        user.setPassword(token.getPassword()); // đã được encode ở OtpService
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        userService.save(user);
        pendingUserRepository.delete(token);

        model.addAttribute("success", "Xác thực thành công! Bạn có thể đăng nhập.");
        return "redirect:/login"; // quay về trang login
    }

}
