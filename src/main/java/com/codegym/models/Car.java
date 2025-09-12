package com.codegym.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Tên xe
    private String model;       // Dòng xe
    private int year;           // Đời xe
    private int mileage;        // Số km đã chạy
    @Column(name = "`condition`")  // escape keyword
    private String condition;  // Tình trạng (mới/cũ)
    private double price;       // Giá bán
    private String transmission; // Hộp số (số sàn/số tự động)
    private String imageUrl;// Ảnh

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;        // Hãng xe (Toyota, Ford…)
}
