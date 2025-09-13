package com.codegym.services;

import com.codegym.dto.ProfileDTO;
import com.codegym.dto.UserDTO;
import com.codegym.models.User;
import com.codegym.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String UPLOAD_DIR = "/Users/mac/Documents/Sales-Management/src/main/resources/uploads/user/"; // đường dẫn thư mục upload

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUploadService fileUploadService;


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

    // Tìm kiếm user (tên, email, phone)
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                keyword, keyword, keyword
        );
    }
}
