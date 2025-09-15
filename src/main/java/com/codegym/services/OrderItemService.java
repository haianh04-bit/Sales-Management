package com.codegym.services;

import com.codegym.models.OrderItem;
import com.codegym.models.User;
import com.codegym.repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItemsByUser(User user) {
        return orderItemRepository.findByOrderUser(user);
    }
}
