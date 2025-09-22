package com.codegym.services;

import com.codegym.dto.ProfileDTO;
import com.codegym.dto.UserDTO;
import com.codegym.models.User;
import com.codegym.repositories.UserRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final String UPLOAD_DIR = "D:/module4/case-study-module4/src/main/resources/uploads/users/"; // đường dẫn thư mục upload

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final MailService mailService;

    public UserService(UserRepository userRepository, FileUploadService fileUploadService, MailService mailService) {
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.mailService = mailService;
    }

    // --- Dùng cho Spring Security ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Trả về UserDetails để Spring Security xác thực
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // dùng email login
                .password(user.getPassword()) // password đã mã hoá
                .roles(user.getRole().name().replace("ROLE_", "")) // ROLE_USER => USER
                .build();
    }

    // --- CRUD + logic riêng ---
    // Tìm user theo email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Tìm user theo id
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Lấy profile
    public User getProfile(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Cập nhật thông tin cá nhân (chỉ cho profile)
    public void updateProfile(Long id, ProfileDTO dto) throws IOException {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setUsername(dto.getUsername());
        currentUser.setPhone(dto.getPhone());
        currentUser.setAddress(dto.getAddress());

        MultipartFile file = dto.getAvatarFile();
        if (file != null && !file.isEmpty()) {
            if (currentUser.getImageUrl() != null) {
                fileUploadService.deleteFile(UPLOAD_DIR + "/" + currentUser.getImageUrl());
            }
            String fileName = fileUploadService.uploadFile(UPLOAD_DIR, file);
            currentUser.setImageUrl(fileName);
        }

        userRepository.save(currentUser);
    }


    // Xoá tài khoản
    public void deleteAccount(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getImageUrl() != null) {
                fileUploadService.deleteFile(UPLOAD_DIR + "/" + user.getImageUrl());
            }
            userRepository.delete(user);
        }
    }

    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAllUsersExcludingAdmin(
                PageRequest.of(page, size, Sort.by("id").descending())
        );
    }

    // Tìm kiếm user có phân trang
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                keyword, keyword, keyword, pageable
        );
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Nội dung email
            String subject = "Thông báo xoá tài khoản";
            String body = "Xin chào " + user.getUsername() + ",\n\n"
                    + "Tài khoản của bạn đã bị xoá khỏi hệ thống. "
                    + "Nếu có thắc mắc, vui lòng liên hệ bộ phận hỗ trợ.\n\n"
                    + "Trân trọng!";

            // Gửi email
            mailService.sendMail(user.getEmail(), subject, body);

            // Xoá user
            userRepository.delete(user);
        } else {
            throw new RuntimeException("Không tìm thấy user với id = " + id);
        }
    }

}