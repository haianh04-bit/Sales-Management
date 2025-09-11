package com.codegym.controller;

import com.codegym.dto.ProductDTO;
import com.codegym.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ✅ Danh sách sản phẩm (có phân trang)
    @GetMapping
    public String listProducts(@PageableDefault(size = 5) Pageable pageable, Model model) {
        model.addAttribute("products", productService.findAll(pageable));
        return "products/list"; // -> /templates/product/list.html
    }

    // ✅ Form thêm mới sản phẩm
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        return "products/create";
    }

    // ✅ Xử lý thêm mới sản phẩm
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            return "products/create";
        }

        productService.createProduct(productDTO);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        return "redirect:/products";
    }

    // ✅ Form chỉnh sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ProductDTO> dtoOpt = productService.findByIdDTO(id);
        if (dtoOpt.isPresent()) {
            model.addAttribute("productDTO", dtoOpt.get());
            return "products/edit";
        } else {
            return "redirect:/products";
        }
    }

    // ✅ Xử lý cập nhật sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            return "products/edit";
        }

        productService.updateProduct(id, productDTO);
        redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        return "redirect:/products";
    }

    // ✅ Xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        return "redirect:/products";
    }

    // ✅ Xem chi tiết sản phẩm
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Optional<ProductDTO> dtoOpt = productService.findByIdDTO(id);
        if (dtoOpt.isPresent()) {
            model.addAttribute("product", dtoOpt.get());
            return "products/view";
        } else {
            return "redirect:/products";
        }
    }
}
