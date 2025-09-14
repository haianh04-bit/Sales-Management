package com.codegym.controller;

import com.codegym.models.Car;
import com.codegym.models.Review;
import com.codegym.models.User;
import com.codegym.services.CarService;
import com.codegym.services.ReviewService;
import com.codegym.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CarService carService;
    private final UserService userService;

    // Thêm đánh giá
    @PostMapping("/add/{carId}")
    public String addReview(@PathVariable Long carId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // chưa login → redirect
        }

        // Lấy user từ email (principal.getName())
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/login"; // không tìm thấy user trong DB
        }

        Car car = carService.findById(carId);
        reviewService.saveReview(user, car, rating, comment);

        return "redirect:/cars/view/" + carId;
    }

    // Xóa đánh giá
    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Review review = reviewService.findById(reviewId);
        User user = userService.findByEmail(principal.getName());

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xoá đánh giá này!");
        }

        Long carId = review.getCar().getId();
        reviewService.deleteReview(reviewId);

        return "redirect:/cars/view/" + carId;
    }
}
