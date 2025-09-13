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

    // Thêm vào giỏ hàng nhưng ở lại trang cũ
    @PostMapping("/add")
    public String addToCart(@RequestParam Long carId,
                            @RequestParam int quantity,
                            @AuthenticationPrincipal User user,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        cartService.addToCart(user, carId, quantity);

        // Quay về trang trước (home, list...)
        if (referer != null) {
            return "redirect:" + referer;
        }
        return "redirect:/cars";
    }

    // Mua ngay → thêm vào giỏ rồi chuyển sang trang giỏ hàng
    @PostMapping("/buy")
    public String buyNow(@RequestParam Long carId,
                         @RequestParam int quantity,
                         @AuthenticationPrincipal User user) {
        cartService.addToCart(user, carId, quantity);
        return "redirect:/cart"; // luôn sang giỏ hàng
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
        var cartItems = cartService.getCart(user);
        double total = cartItems.stream()
                .mapToDouble(i -> i.getCar().getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
        return "cart/checkout";
    }


    // Xử lý thanh toán và tạo đơn hàng
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        Order order = orderService.checkout(user);
        redirectAttributes.addFlashAttribute("message", "Order placed successfully! ID: " + order.getId());
        return "redirect:/cart";
    }

    // Thêm nhanh vào giỏ bằng GET (cho link từ home.html)
    @GetMapping("/add/{carId}")
    public String addToCartQuick(@PathVariable Long carId,
                                 @AuthenticationPrincipal User user) {
        cartService.addToCart(user, carId, 1); // mặc định số lượng = 1
        return "redirect:/cart";
    }

    // Mua ngay: thêm vào giỏ rồi đi thẳng tới checkout
    @GetMapping("/buy/{carId}")
    public String buyNow(@PathVariable Long carId,
                         @AuthenticationPrincipal User user) {
        cartService.addToCart(user, carId, 1);
        return "redirect:/cart/checkout";
    }


    @PostMapping("/decrease")
    public String decreaseQuantity(@RequestParam Long cartItemId) {
        cartService.decreaseQuantity(cartItemId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId,
                                 @RequestParam int quantity,
                                 @AuthenticationPrincipal User user) {
        if (quantity > 0) {
            cartService.updateQuantity(cartItemId, quantity);
        } else {
            cartService.removeFromCart(cartItemId);
        }
        return "redirect:/cart";
    }

}
