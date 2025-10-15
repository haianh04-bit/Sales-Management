package com.codegym.services;

import com.codegym.models.*;
import com.codegym.repositories.CarRepository;
import com.codegym.repositories.CartItemRepository;
import com.codegym.repositories.OrderItemRepository;
import com.codegym.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository, CarRepository carRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.cartItemRepository = cartItemRepository;
    }

    //thanh toán giỏ hàng và tạo đơn hàng
    @Transactional
    public Order checkout(User user, List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Tạo Order mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Đang chờ xác nhận");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem ci : cartItems) {
            var car = carRepository.findById(ci.getCar().getId())
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));

            if (car.getQuantity() < ci.getQuantity()) {
                throw new RuntimeException("Không đủ số lượng cho xe: " + car.getName());
            }

            // Trừ số lượng kho
            car.setQuantity(car.getQuantity() - ci.getQuantity());
            carRepository.save(car);

            // Tạo OrderItem
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setCar(car);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(car.getPrice());
            orderItems.add(oi);

            total += ci.getQuantity() * car.getPrice();
        }

        order.setTotalPrice(total);
        order.setItems(orderItems);

        // Lưu Order + OrderItem
        Order savedOrder = orderRepository.save(order);

        // Xoá giỏ hàng sau khi lưu thành công
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }


    public List<Order> findByUser(User user) {
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

    public List<Order> getAllOrdersWithUserAndItems() {
        return orderRepository.findAllWithUserAndItems();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public long countOrdersByUser(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    public void cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền hủy: chỉ chủ đơn mới được hủy
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        // Không cho hủy nếu đã xác nhận hoặc đang giao
        if (!order.getStatus().equals("Đang chờ xác nhận")) {
            throw new RuntimeException("Đơn hàng đã được xác nhận, không thể hủy");
        }

        // Nếu cho phép hủy
        order.setStatus("Đã hủy");
        order.setCancelTime(LocalDateTime.now());

        order.getItems().forEach(item -> {
            var car = item.getCar();
            car.setQuantity(car.getQuantity() + item.getQuantity());
        });

        orderRepository.save(order);
    }

}