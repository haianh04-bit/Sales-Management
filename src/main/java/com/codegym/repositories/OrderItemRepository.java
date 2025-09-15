package com.codegym.repositories;

import com.codegym.models.OrderItem;
import com.codegym.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user = :user ORDER BY o.id DESC")
    List<OrderItem> findByOrderUser(@Param("user") User user);
}
