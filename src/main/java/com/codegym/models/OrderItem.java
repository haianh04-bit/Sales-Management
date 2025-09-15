package com.codegym.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tham chiếu đến xe
    @ManyToOne(fetch = FetchType.LAZY)   // lazy để tránh load nặng
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private int quantity;

    private double price; // snapshot giá tại thời điểm mua

    // Tham chiếu đến Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
