package com.codegym.services;

import com.codegym.models.User;
import com.codegym.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final String UPLOAD_DIR = "D:/module4/case-study/uploads/user";

    @Autowired
    private UserRepository userRepository;


    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Tìm user theo email (Spring Security sẽ dùng)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // dùng email làm username trong Spring Security
                .password(user.getPassword()) // password đã mã hoá BCrypt trong DB
                .roles(user.getRole().name().replace("ROLE_", "")) // ví dụ USER
                .disabled(!user.isEnabled())
                .build();
    }
}
