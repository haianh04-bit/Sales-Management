package com.codegym.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1 - 5 sao

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Người dùng đánh giá
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Sản phẩm được đánh giá
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
