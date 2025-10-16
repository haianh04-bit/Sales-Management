package com.codegym.services;

import com.codegym.models.Car;
import com.codegym.models.CartItem;
import com.codegym.models.User;
import com.codegym.repositories.CarRepository;
import com.codegym.repositories.CartItemRepository;
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

    // Thêm xe vào giỏ hàng, chỉ lưu CartItem, không trừ kho
    public void addToCart(User user, Long carId, int quantity) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));

        if (car.getQuantity() <= 0) {
            throw new RuntimeException("Xe đã hết hàng");
        }

        // Tìm sản phẩm đã có trong giỏ hàng của user
        CartItem cartItem = cartItemRepository.findByUserAndCar(user, car).orElse(null);

        int totalQuantity = quantity;
        if (cartItem != null) {
            totalQuantity += cartItem.getQuantity();
        }

        // Kiểm tra vượt quá tồn kho
        if (totalQuantity > car.getQuantity()) {
            throw new RuntimeException("Số lượng vượt quá số xe còn trong kho!");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setCar(car);
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(totalQuantity);
        }

        cartItemRepository.save(cartItem);
    }


    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUserId(user.getId());
    }

    // Xóa xe khỏi giỏ hàng → không ảnh hưởng kho
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // Xóa toàn bộ giỏ hàng → không ảnh hưởng kho
    public void clearCart(User user) {
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        cartItemRepository.deleteAll(items);
    }

    // Giảm số lượng trong giỏ hàng
    public void decreaseQuantity(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
        } else {
            cartItemRepository.delete(item);
        }
    }

    // Cập nhật số lượng trong giỏ hàng
    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found: " + cartItemId));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // Lấy tất cả CartItem kèm User và Car
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAllWithUserAndCar();
    }
}
