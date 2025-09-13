package com.codegym.dto;

import com.codegym.models.Role;
import com.codegym.validations.custom.Image;
import com.codegym.validations.custom.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id; // Sửa từ int sang Long

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}\\s\\.-]*$",
            message = "Tên người dùng chỉ được chứa chữ cái (có dấu), khoảng trắng, dấu chấm hoặc gạch ngang")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @UniqueEmail(message = "Email đã được sử dụng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;   // <--- thêm password

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Image
    private MultipartFile avatarFile;  // dùng khi upload ảnh mới

    private String imageUrl; // dùng khi trả dữ liệu về client (ảnh cũ đã lưu)


    private boolean enabled;
    private Role role;

    // Constructor cho trường hợp tạo mới user
    public UserDTO(Long id, String username, String email, String phone, String address, String imageUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.imageUrl = imageUrl;
    }
}
