package com.codegym.dto;

import com.codegym.validations.custom.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {


    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}\\s\\.-]*$",
            message = "Tên người dùng chỉ được chứa chữ cái (có dấu), khoảng trắng, dấu chấm hoặc gạch ngang")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @UniqueEmail(message = "Email đã được sử dụng")
    private String email;

}
