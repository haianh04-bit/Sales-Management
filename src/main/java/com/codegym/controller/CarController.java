package com.codegym.controller;

import com.codegym.dto.CarDTO;
import com.codegym.mapper.CarMapper;
import com.codegym.models.Car;
import com.codegym.services.BrandService;
import com.codegym.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final BrandService brandService;
    private final CarMapper carMapper;

    // Hiển thị danh sách xe
    @GetMapping
    public String listCars(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "car/list";
    }

    // Hiển thị form tạo xe mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("car", new CarDTO());
        model.addAttribute("brands", brandService.findAll());
        return "car/create";
    }

    // Thêm xe mới
    @PostMapping("/create")
    public String createCar(@Valid @ModelAttribute("car") CarDTO carDTO,
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.findAll());
            return "car/create";
        }

        try {
            carService.saveCar(carDTO, imageFile);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
            model.addAttribute("brands", brandService.findAll());
            return "car/create";
        }

        return "redirect:/cars";
    }

    // Hiển thị form sửa xe
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Car car = carService.findById(id);
        CarDTO dto = carMapper.toDTO(car); // map entity -> DTO
        if (car.getBrand() != null) {
            dto.setBrandName(car.getBrand().getName()); // brandName là String
        }
        model.addAttribute("car", dto);
        model.addAttribute("brands", brandService.findAll());
        return "car/edit";
    }

    // Cập nhật xe
    @PostMapping("/edit/{id}")
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("car") CarDTO carDTO,
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.findAll());
            return "car/edit";
        }

        try {
            carService.updateCar(id, carDTO, imageFile);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
            model.addAttribute("brands", brandService.findAll());
            return "car/edit";
        }

        return "redirect:/cars/view/" + id;
    }

    // Xoá xe
    @GetMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        carService.deleteById(id);
        return "redirect:/cars";
    }

    // Xem chi tiết xe
    @GetMapping("/view/{id}")
    public String viewCar(@PathVariable Long id, Model model) {
        Car car = carService.findById(id);
        model.addAttribute("car", car);
        return "car/view";
    }

    @GetMapping("/search")
    public String searchCars(@RequestParam(value = "brand", required = false) String brand,
                             @RequestParam(value = "model", required = false) String modelName,
                             @RequestParam(value = "year", required = false) Integer year,
                             @RequestParam(value = "condition", required = false) String condition,
                             @RequestParam(value = "transmission", required = false) String transmission,
                             @RequestParam(value = "minPrice", required = false) Double minPrice,
                             @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                             Model model) {

        List<Car> cars = carService.searchCars(brand, modelName, year, condition, transmission, minPrice, maxPrice);
        model.addAttribute("cars", cars);
        model.addAttribute("brands", brandService.findAll());

        // Giữ lại giá trị search trên form
        model.addAttribute("brandSelected", brand);
        model.addAttribute("modelSelected", modelName);
        model.addAttribute("yearSelected", year);
        model.addAttribute("conditionSelected", condition);
        model.addAttribute("transmissionSelected", transmission);
        model.addAttribute("minPriceSelected", minPrice);
        model.addAttribute("maxPriceSelected", maxPrice);

        return "car/list";
    }
}
