package com.codegym.services;

import com.codegym.models.CartItem;
import com.codegym.models.Car;
import com.codegym.models.User;
import com.codegym.repositories.CartItemRepository;
import com.codegym.repositories.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final CarRepository carRepository;

    public CartService(CartItemRepository cartItemRepository, CarRepository carRepository) {
        this.cartItemRepository = cartItemRepository;
        this.carRepository = carRepository;
    }

    //thêm xe vào giỏ hàng
    public void addToCart(User user, Long carId, int quantity) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        CartItem cartItem = cartItemRepository.findByUser(user).stream()
                .filter(item -> item.getCar().getId().equals(carId))
                .findFirst()
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setUser(user);
            cartItem.setCar(car);
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
    }

    //lấy danh sách xe trong giỏ hàng của người dùng
    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    //xóa xe khỏi giỏ hàng
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    //xóa tất cả xe trong giỏ hàng của người dùng
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
