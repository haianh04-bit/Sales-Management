package com.codegym.services;

import com.codegym.dto.ProductDTO;
import com.codegym.mapper.ProductMapper;
import com.codegym.models.Product;
import com.codegym.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ProductService {

    private static final String UPLOAD_DIR = "D:/module4/case-study-module4/src/main/resources/uploads/products/";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileUploadService fileUploadService;

    // ✅ Lấy tất cả sản phẩm (phân trang)
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toDTO);
    }

    // ✅ Lấy sản phẩm theo ID (entity gốc)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ Lấy sản phẩm theo ID (DTO)
    public Optional<ProductDTO> findByIdDTO(Long id) {
        return productRepository.findById(id).map(ProductMapper::toDTO);
    }

    // ✅ Thêm mới sản phẩm
    public void createProduct(ProductDTO dto) throws IOException {
        Product product = ProductMapper.toEntity(dto);

        MultipartFile file = dto.getImageFile();
        if (file != null && !file.isEmpty()) {
            String fileName = fileUploadService.uploadFile(UPLOAD_DIR, file);
            product.setImageUrl(fileName);
        }

        productRepository.save(product);
    }

    // ✅ Cập nhật sản phẩm
    public void updateProduct(Long id, ProductDTO dto) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        MultipartFile file = dto.getImageFile();
        if (file != null && !file.isEmpty()) {
            // Xóa ảnh cũ
            if (product.getImageUrl() != null) {
                fileUploadService.deleteFile(UPLOAD_DIR + "/" + product.getImageUrl());
            }
            String fileName = fileUploadService.uploadFile(UPLOAD_DIR, file);
            product.setImageUrl(fileName);
        }

        productRepository.save(product);
    }

    // ✅ Xóa sản phẩm
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (product.getImageUrl() != null) {
            fileUploadService.deleteFile(UPLOAD_DIR + "/" + product.getImageUrl());
        }

        productRepository.delete(product);
    }
}
