package com.codegym.controller;

import com.codegym.models.CartItem;
import com.codegym.models.Order;
import com.codegym.models.User;
import com.codegym.repositories.OrderRepository;
import com.codegym.services.CartService;
import com.codegym.services.DashboardService;
import com.codegym.services.OrderService;
import com.codegym.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final DashboardService dashboardService;
    private final CartService cartService;
    private final OrderService orderService;

    public AdminController(UserService userService, DashboardService dashboardService, CartService cartService, OrderService orderService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.cartService = cartService;
        this.orderService = orderService;
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
        model.addAttribute("totalPages", userPage.getTotalPages());

        return "admin/user-list";
    }

    // Tìm kiếm user
    @GetMapping("/users/search")
    public String searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
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

    @GetMapping("/carts")
    public String listAllCarts(Model model) {
        List<CartItem> allCartItems = cartService.getAllCartItems();
        model.addAttribute("cartItems", allCartItems);
        return "admin/cart-list";
    }

    @GetMapping("/orders")
    public String listAllOrders(Model model) {
        List<Order> allOrders = orderService.getAllOrdersWithUserAndItems();
        model.addAttribute("orders", allOrders);
        return "admin/order-list";
    }

    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, "Đã xác nhận");
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xoá user thành công và đã gửi thông báo đến email!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users"; // quay lại trang danh sách user
    }
}
