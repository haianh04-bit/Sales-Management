package com.codegym.repositories;

import com.codegym.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    @Query("SELECT COUNT(c) FROM Car c")
    Long countCars();
    List<Car> findTop6ByOrderByIdDesc();
    Optional<Car> findByNameAndModelAndYearAndTransmissionAndConditionAndPrice(
            String name,
            String model,
            int year,
            String transmission,
            String condition,
            double price
    );

}
