package com.codegym.services;


import com.codegym.models.Order;
import com.codegym.repositories.CarRepository;
import com.codegym.repositories.OrderRepository;
import com.codegym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Long getTotalCars() {
        return carRepository.countCars();
    }

    public Long getTotalCustomers() {
        return userRepository.countCustomers();
    }

    public long getTotalOrders() {
        return orderRepository.countByStatus("Đã xác nhận");
    }
    public double getTotalRevenue() {
        List<Order> confirmedOrders = orderRepository.findByStatus("Đã xác nhận");
        return confirmedOrders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }
}
