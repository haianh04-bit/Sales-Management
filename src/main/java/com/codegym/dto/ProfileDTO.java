package com.codegym.dto;

import com.codegym.validations.custom.Image;
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
public class ProfileDTO {
    private Long id;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Image
    private MultipartFile avatarFile;  // ảnh upload mới

    private String imageUrl; // ảnh cũ đã lưu
}
