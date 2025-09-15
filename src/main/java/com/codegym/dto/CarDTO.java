package com.codegym.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {

    private Long id;

    @NotBlank(message = "Tên xe không được để trống")
    @Size(min = 2, max = 100, message = "Tên xe phải từ 2 đến 100 ký tự")
    private String name;

    @NotNull(message = "Hãng xe không được để trống")
    private Long brandId;   // dùng cho form (select option)

    private String brandName ;  // id của hãng xe (Toyota, Ford...)

    @NotBlank(message = "Dòng xe không được để trống")
    private String model;

    @NotNull(message = "Đời xe không được để trống")
    @Min(value = 1990, message = "Đời xe phải >= 1990")
    @Max(value = 2025, message = "Đời xe không hợp lệ")
    private Integer year;

    @NotNull(message = "Số km không được để trống")
    @PositiveOrZero(message = "Số km phải >= 0")
    private Integer mileage;

    @NotBlank(message = "Tình trạng xe không được để trống")
    @Pattern(regexp = "mới|cũ", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Tình trạng chỉ được nhập: mới hoặc cũ")
    private String condition;

    @NotNull(message = "Giá xe không được để trống")
    @Positive(message = "Giá xe phải > 0")
    private Double price;

    // Upload file
    private MultipartFile imageFile;

    private String imageUrl;// tên file ảnh đã lưu

    private int quantity;

    // Mô tả chi tiết
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    private String transmission;

    private double averageRating;
    private int fullStars;
    private boolean halfStar;
    private int emptyStars;

}
