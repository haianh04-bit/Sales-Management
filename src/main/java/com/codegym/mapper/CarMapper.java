package com.codegym.mapper;

import com.codegym.dto.CarDTO;
import com.codegym.models.Car;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    // Map Car -> CarDTO
    public CarDTO toDTO(Car car) {
        if (car == null) return null;


        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setName(car.getName());

        if (car.getBrand() != null) {
            dto.setBrandId(car.getBrand().getId());   // set brandId cho form
            dto.setBrandName(car.getBrand().getName()); // set brandName để hiển thị
        }
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());
        dto.setMileage(car.getMileage());
        dto.setCondition(car.getCondition());
        dto.setPrice(car.getPrice());
        dto.setDescription(car.getDescription());
        dto.setTransmission(car.getTransmission());
        dto.setImageFile(null); // MultipartFile không map trực tiếp
        dto.setImageUrl(car.getImageUrl()); // map từ entity sang DTO
        dto.setQuantity(car.getQuantity()); // Thêm số lượng
        return dto;
    }

    // Map CarDTO -> Car (mới hoặc update)
    public Car toEntity(CarDTO dto) {
        if (dto == null) return null;

        Car car = new Car();
        car.setId(dto.getId());
        car.setName(dto.getName());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setMileage(dto.getMileage());
        car.setCondition(dto.getCondition());
        car.setPrice(dto.getPrice());
        car.setDescription(dto.getDescription());
        car.setTransmission(dto.getTransmission());
        // brand và imageUrl sẽ được set trong service
        return car;
    }

    // Update entity từ DTO (không thay đổi brand và imageUrl)
    public void updateEntity(Car car, CarDTO dto) {
        if (car == null || dto == null) return;

        car.setName(dto.getName());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setMileage(dto.getMileage());
        car.setCondition(dto.getCondition());
        car.setPrice(dto.getPrice());
        car.setDescription(dto.getDescription());
        car.setTransmission(dto.getTransmission());
        car.setQuantity(dto.getQuantity());
    }
}
