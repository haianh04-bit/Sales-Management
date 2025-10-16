package com.codegym.repositories;

import com.codegym.models.Car;
import com.codegym.models.CartItem;
import com.codegym.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    void deleteByUser(User user);
    @Query("SELECT c FROM CartItem c JOIN FETCH c.user u JOIN FETCH c.car car ORDER BY u.id, c.id")
    List<CartItem> findAllWithUserAndCar();
    Optional<CartItem> findByUserAndCar(User user, Car car);

}
