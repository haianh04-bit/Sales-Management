package com.codegym.controller;

import com.codegym.models.User;
import com.codegym.models.Order;
import com.codegym.services.CartService;
import com.codegym.services.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    // Hiển thị giỏ hàng
    @GetMapping
    public String viewCart(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("cartItems", cartService.getCart(user));
        return "cart/cart";
    }

    // Thêm xe vào giỏ hàng
    @PostMapping("/add")
    public String addToCart(@RequestParam Long carId,
                            @RequestParam int quantity,
                            @AuthenticationPrincipal User user) {
        cartService.addToCart(user, carId, quantity);
        return "redirect:/cart";
    }

    // Xóa xe khỏi giỏ hàng
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return "redirect:/cart";
    }

    // Hiển thị trang thanh toán
    @GetMapping("/checkout")
    public String checkoutPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("cartItems", cartService.getCart(user));
        return "cart/checkout";
    }

    // Xử lý thanh toán và tạo đơn hàng
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        Order order = orderService.checkout(user);
        redirectAttributes.addFlashAttribute("message", "Order placed successfully! ID: " + order.getId());
        return "redirect:/cart";
    }
}
