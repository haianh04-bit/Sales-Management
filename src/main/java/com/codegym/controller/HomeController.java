package com.codegym.controller;

import com.codegym.dto.CarDTO;
import com.codegym.models.Car;
import com.codegym.services.CarService;
import com.codegym.services.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final CarService carService;
    private final ReviewService reviewService;

    public HomeController(CarService carService, ReviewService reviewService) {
        this.carService = carService;
        this.reviewService = reviewService;
    }

    @GetMapping(value = {"/", "/home"})
    public String home(Model model) {
        List<Car> cars = carService.getNewCars();

        List<CarDTO> carDTOs = cars.stream().map(car -> {
            double avg = reviewService.getAverageRatingByCar(car.getId()); // service tính trung bình
            int fullStars = (int) avg;
            boolean halfStar = (avg - fullStars) >= 0.5;
            int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

            return CarDTO.builder()
                    .id(car.getId())
                    .name(car.getName())
                    .imageUrl(car.getImageUrl())
                    .price(car.getPrice())
                    .quantity(car.getQuantity())
                    .averageRating(avg)
                    .fullStars(fullStars)
                    .halfStar(halfStar)
                    .emptyStars(emptyStars)
                    .build();
        }).toList();

        model.addAttribute("newCars", carDTOs);
        return "home";
    }

}

