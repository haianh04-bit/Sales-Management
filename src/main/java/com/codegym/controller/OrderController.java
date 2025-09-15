package com.codegym.controller;

import com.codegym.models.Order;
import com.codegym.models.OrderItem;
import com.codegym.models.User;
import com.codegym.services.OrderItemService;
import com.codegym.services.OrderService;
import com.codegym.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderItemService orderItemService;

    public OrderController(OrderService orderService, UserService userService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderItemService = orderItemService;
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("orders", orderService.findByUser(user));
        return "order/list";
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
