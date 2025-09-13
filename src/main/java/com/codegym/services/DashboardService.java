package com.codegym.services;


import com.codegym.repositories.CarRepository;
import com.codegym.repositories.OrderRepository;
import com.codegym.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Long getTotalOrders() {
        return orderRepository.countOrders();
    }

    public Long getTotalRevenue() {
        return orderRepository.totalRevenue() != null ? orderRepository.totalRevenue() : 0L;
    }
}
