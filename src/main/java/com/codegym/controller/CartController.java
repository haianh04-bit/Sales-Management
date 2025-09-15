package com.codegym.controller;

import com.codegym.models.CartItem;
import com.codegym.models.User;
import com.codegym.models.Order;
import com.codegym.services.CartService;
import com.codegym.services.OrderService;
import com.codegym.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public CartController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    // Hiển thị giỏ hàng
    @GetMapping
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login"; // chưa login
        }
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("cartItems", cartService.getCart(user));
        return "cart/cart";
    }

    // Thêm vào giỏ hàng nhưng ở lại trang cũ
    @PostMapping("/add")
    public String addToCart(@RequestParam Long carId,
                            @RequestParam int quantity,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestHeader(value = "Referer", required = false) String referer,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.addToCart(user, carId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm xe vào giỏ hàng!");
        if (referer != null) return "redirect:" + referer;
        return "redirect:/cars";
    }

    // Mua ngay → thêm vào giỏ rồi chuyển sang trang giỏ hàng
    @PostMapping("/buy")
    public String buyNow(@RequestParam Long carId,
                         @RequestParam int quantity,
                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.addToCart(user, carId, quantity);
        return "redirect:/cart";
    }

    // Xóa sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.removeFromCart(cartItemId);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa xe khỏi giỏ hàng thành công!");
        return "redirect:/cart";
    }

    // Hiển thị trang thanh toán
    @GetMapping("/checkout")
    public String checkoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<CartItem> cartItems = cartService.getCart(user);
        double total = cartItems.stream()
                .mapToDouble(i -> i.getCar().getPrice() * i.getQuantity())
                .sum();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
        return "cart/checkout";
    }

    // Thanh toán
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<CartItem> cartItems = cartService.getCart(user);

        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống, không thể đặt hàng!");
            return "redirect:/cart";
        }

        try {
            Order order = orderService.checkout(user, cartItems);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt hàng thành công! Mã đơn: " + order.getId());
            return "redirect:/orders/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    // Thêm nhanh vào giỏ bằng GET
    @GetMapping("/add/{carId}")
    public String addToCartQuick(@PathVariable Long carId,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.addToCart(user, carId, 1);
        return "redirect:/cart";
    }

    // Mua ngay → thêm vào giỏ rồi tới checkout
    @GetMapping("/buy/{carId}")
    public String buyNow(@PathVariable Long carId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.addToCart(user, carId, 1);
        return "redirect:/cart/checkout";
    }

    // Giảm số lượng
    @PostMapping("/decrease")
    public String decreaseQuantity(@RequestParam Long cartItemId,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.decreaseQuantity(cartItemId);
        return "redirect:/cart";
    }

    // Cập nhật số lượng
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId,
                                 @RequestParam int quantity,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (quantity > 0) {
            cartService.updateQuantity(cartItemId, quantity);
        } else {
            cartService.removeFromCart(cartItemId);
        }
        return "redirect:/cart";
    }
}
