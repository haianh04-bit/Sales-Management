package com.codegym.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDateTime orderDate;

    private double totalPrice;

    private String status; // NEW, PAID, SHIPPED, CANCELLED

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id") // để CartItem biết thuộc Order nào
    private List<CartItem> items;
}
