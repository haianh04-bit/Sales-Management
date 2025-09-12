package com.codegym.repositories;

import com.codegym.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    List<Car> findByBrandName(String brandName);
    List<Car> findByPriceBetween(double min, double max);
    List<Car> findByYear(int year);
    List<Car> findByCondition(String condition);
    List<Car> findByTransmission(String transmission);
}
