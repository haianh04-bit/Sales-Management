package com.codegym.dto;

import com.codegym.validations.custom.Image;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id; // Có thể null khi tạo mới

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 1000, message = "Giá sản phẩm phải lớn hơn 1000 VNĐ")
    private Double price;

    private String description;

    private String imageUrl;

    @Image
    private MultipartFile imageFile;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer quantity;
}

