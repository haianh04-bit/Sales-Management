package com.codegym.controller;

import com.codegym.models.Car;
import com.codegym.services.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final CarService carService;

    public HomeController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "home";
    }
}

