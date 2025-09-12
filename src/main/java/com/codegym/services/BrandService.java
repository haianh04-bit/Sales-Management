package com.codegym.services;

import com.codegym.models.Brand;
import com.codegym.repositories.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    // Lấy tất cả brand
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    // Tìm brand theo id
    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy hãng xe với id: " + id));
    }

    public Brand findByName(String name) {
        return (Brand) brandRepository.findByName(name).orElse(null);
    }

    // Tạo brand mới
    @Transactional
    public Brand createBrand(String name) {
        // Kiểm tra trùng
        if (brandRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("❌ Hãng xe này đã tồn tại!");
        }

        Brand brand = new Brand();
        brand.setName(name);
        return brandRepository.save(brand);
    }

    // Cập nhật brand
    @Transactional
    public Brand updateBrand(Long id, String name) {
        Brand brand = findById(id);

        // Kiểm tra trùng với brand khác
        if (brandRepository.existsByNameIgnoreCase(name) && !brand.getName().equalsIgnoreCase(name)) {
            throw new RuntimeException("❌ Hãng xe này đã tồn tại!");
        }

        brand.setName(name);
        return brandRepository.save(brand);
    }

    // Xoá brand
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = findById(id);

        // TODO: kiểm tra ràng buộc với Car nếu cần
        // if(carRepository.existsByBrandId(id)) { throw new RuntimeException("Hãng xe đang có xe, không thể xoá!"); }

        brandRepository.delete(brand);
    }
}
