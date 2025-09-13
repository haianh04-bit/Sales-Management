package com.codegym.repositories;

import com.codegym.models.Order;
import com.codegym.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT COUNT(o) FROM Order o")
    Long countOrders();

    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    Long totalRevenue();
}
