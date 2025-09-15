package com.codegym.repositories;

import com.codegym.models.Car;
import com.codegym.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCar(Car car);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.car.id = :carId")
    Double findAverageRatingByCarId(@Param("carId") Long carId);
}
