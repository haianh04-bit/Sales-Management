package com.codegym.services;

import com.codegym.dto.CarDTO;
import com.codegym.mapper.CarMapper;
import com.codegym.models.Brand;
import com.codegym.models.Car;
import com.codegym.repositories.BrandRepository;
import com.codegym.repositories.CarRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarService {

    private static final String UPLOAD_DIR = "D:/module4/case-study-module4/src/main/resources/uploads/cars/";


    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final CarMapper carMapper;
    private final FileUploadService fileUploadService;
    private final BrandService brandService;

    public CarService(CarRepository carRepository,
                      BrandRepository brandRepository,
                      CarMapper carMapper,
                      FileUploadService fileUploadService, BrandService brandService) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.carMapper = carMapper;
        this.fileUploadService = fileUploadService;
        this.brandService = brandService;
    }

    // Lấy danh sách tất cả xe
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    // Tìm xe theo id
    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với id: " + id));
    }

    // Lưu xe mới từ DTO và ảnh
    public Car saveCar(CarDTO dto, MultipartFile imageFile) throws IOException {
        Car car = carMapper.toEntity(dto);

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with id=" + dto.getBrandId()));
            car.setBrand(brand);
        } else {
            throw new RuntimeException("BrandId is required!");
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileUploadService.uploadFile(UPLOAD_DIR, imageFile);
            car.setImageUrl(fileName);
        }
        return carRepository.save(car);
    }


    // Cập nhật xe từ DTO, giữ nguyên ảnh nếu không upload mới
    public Car updateCar(Long id, CarDTO dto, MultipartFile imageFile) throws IOException {
        Car car = findById(id);
        carMapper.updateEntity(car, dto);

        // Cập nhật brand nếu có brandId
        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with id=" + dto.getBrandId()));
            car.setBrand(brand);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            if (car.getImageUrl() != null) {
                fileUploadService.deleteFile(UPLOAD_DIR + car.getImageUrl());
            }
            String fileName = fileUploadService.uploadFile(UPLOAD_DIR, imageFile);
            car.setImageUrl(fileName);
        }

        return carRepository.save(car);
    }



    // Xóa xe
    public void deleteById(Long id) {
        Car car = findById(id);

        // Xóa file ảnh nếu có
        if (car.getImageUrl() != null) {
            fileUploadService.deleteFile(UPLOAD_DIR + car.getImageUrl());
        }

        carRepository.delete(car);
    }

    public List<Car> searchCars(String brandName,
                                String modelName,
                                Integer year,
                                String condition,
                                String transmission,
                                Double minPrice,
                                Double maxPrice) {

        Specification<Car> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (brandName != null && !brandName.isEmpty()) {
                Brand brand = brandService.findByName(brandName);
                if (brand != null) {
                    predicate = cb.and(predicate, cb.equal(root.get("brand"), brand));
                }
            }

            if (modelName != null && !modelName.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("model")), "%" + modelName.toLowerCase() + "%"));
            }

            if (year != null) {
                predicate = cb.and(predicate, cb.equal(root.get("year"), year));
            }

            if (condition != null && !condition.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("condition"), condition));
            }

            if (transmission != null && !transmission.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("transmission"), transmission));
            }

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.ge(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.le(root.get("price"), maxPrice));
            }

            return predicate;
        };

        return carRepository.findAll(spec);
    }

}
