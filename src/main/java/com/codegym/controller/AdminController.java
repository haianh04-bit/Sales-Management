package com.codegym.controller;

import com.codegym.models.User;
import com.codegym.services.DashboardService;
import com.codegym.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final DashboardService dashboardService;

    public AdminController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    // Hiển thị danh sách user
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Page<User> userPage = userService.getAllUsers(page, size);

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);

        return "admin/user-list";
    }

    // Tìm kiếm user
    @GetMapping("/users/search")
    public String searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Page<User> userPage = userService.searchUsers(keyword, page, size);

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "admin/user-list";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCars", dashboardService.getTotalCars());
        model.addAttribute("totalCustomers", dashboardService.getTotalCustomers());
        model.addAttribute("totalOrders", dashboardService.getTotalOrders());
        model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
        return "admin/dashboard";
    }
}
