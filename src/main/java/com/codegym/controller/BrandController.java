package com.codegym.controller;

import com.codegym.models.Brand;
import com.codegym.services.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // Danh sách hãng xe
    @GetMapping
    public String listBrands(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brand/list";
    }

    // Form thêm mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brand/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Brand brand) {
        brandService.save(brand);
        return "redirect:/brands";
    }

    // Form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.findById(id);
        model.addAttribute("brand", brand);
        return "brand/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Brand brand) {
        brand.setId(id);
        brandService.save(brand);
        return "redirect:/brands";
    }

    // Xoá
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        brandService.deleteById(id);
        return "redirect:/brands";
    }
}
