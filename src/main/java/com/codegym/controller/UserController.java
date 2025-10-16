package com.codegym.controller;

import com.codegym.dto.ProfileDTO;
import com.codegym.dto.UserDTO;
import com.codegym.models.User;
import com.codegym.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Trang cá nhân
    @GetMapping("/profile")
    public String profile(HttpSession session, Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) return "redirect:/login";

        UserDTO dto = new UserDTO(
                user.getId(), // Đã là Long
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getImageUrl()
        );

        session.setAttribute("currentUser", user);
        model.addAttribute("user", dto);
        return "users/profile";
    }

    // Form sửa thông tin cá nhân
    @GetMapping("/edit")
    public String editForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) return "redirect:/login";

        ProfileDTO dto = ProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .imageUrl(user.getImageUrl())
                .build();

        model.addAttribute("user", dto);
        return "users/edit";
    }

    // Xử lý cập nhật thông tin cá nhân
    @PostMapping("/edit")
    public String updateProfile(Authentication authentication,
                                @Valid @ModelAttribute("user") ProfileDTO dto,
                                BindingResult result,
                                HttpSession session,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("user", dto);
            return "users/edit";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) return "redirect:/login";

        userService.updateProfile(user.getId(), dto);

        User updatedUser = userService.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found after update"));
        session.setAttribute("currentUser", updatedUser);

        return "redirect:/user/profile?success";
    }


    @GetMapping("/delete")
    public String deleteAccount(Authentication authentication, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user == null) return "redirect:/login";

        userService.deleteAccount(user.getId());
        session.invalidate(); // đăng xuất sau khi xoá

        redirectAttributes.addFlashAttribute("success", "Xoá tài khoản thành công!");
        return "redirect:/login";
    }

    // Xem profile người khác (nếu cần)
    @GetMapping("/{id}")
    public String viewProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getProfile(id);
        if (user == null) {
            return "errors/404";
        }
        UserDTO dto = new UserDTO(
                user.getId(), // Đã là Long
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getImageUrl()
        );
        model.addAttribute("user", dto);
        return "users/profile";
    }
}
