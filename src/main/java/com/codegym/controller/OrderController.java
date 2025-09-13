package com.codegym.controller;

import com.codegym.models.Order;
import com.codegym.models.User;
import com.codegym.services.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Xem lịch sử đơn hàng của user
    @GetMapping("/history")
    public String viewOrders(@AuthenticationPrincipal User user, Model model) {
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order/list"; // cần tạo order/list.html
    }

    // Xem chi tiết 1 đơn hàng
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order/detail"; // cần tạo order/detail.html
    }

    // (Tùy chọn) Admin có thể cập nhật trạng thái đơn hàng
    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/orders/" + id;
    }
}
