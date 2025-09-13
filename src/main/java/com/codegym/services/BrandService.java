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

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }
}
