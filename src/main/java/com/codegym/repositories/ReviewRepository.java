package com.codegym.repositories;

import com.codegym.models.Car;
import com.codegym.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCar(Car car);
}
