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

    @Query("SELECT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.items oi JOIN FETCH oi.car ORDER BY o.id")
    List<Order> findAllWithUserAndItems();

}
