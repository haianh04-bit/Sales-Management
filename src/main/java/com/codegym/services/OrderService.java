package com.codegym.services;

import com.codegym.models.CartItem;
import com.codegym.models.Order;
import com.codegym.models.User;
import com.codegym.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    //thanh toán giỏ hàng và tạo đơn hàng
    public Order checkout(User user) {
        List<CartItem> items = cartService.getCart(user);
        double total = items.stream()
                .mapToDouble(i -> i.getCar().getPrice() * i.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>(items));
        order.setTotalPrice(total);
        order.setStatus("NEW");

        orderRepository.save(order);
        cartService.clearCart(user);

        return order;
    }
//lấy danh sách đơn hàng của người dùng
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
//lấy đơn hàng theo id
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
//cập nhật trạng thái đơn hàng
    public void updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        orderRepository.save(order);
    }

}
