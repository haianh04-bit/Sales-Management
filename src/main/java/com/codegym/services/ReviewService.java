package com.codegym.services;

import com.codegym.models.Car;
import com.codegym.models.Review;
import com.codegym.models.User;
import com.codegym.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // Thêm đánh giá
    public void saveReview(User user, Car car, int rating, String comment) {
        Review review = new Review();
        review.setUser(user);
        review.setCar(car);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
    }

    // Lấy đánh giá theo xe
    public List<Review> getReviewsByCar(Car car) {
        return reviewRepository.findByCar(car);
    }

    // Tìm review theo id
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review"));
    }


    // Xóa review
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public double getAverageRatingByCar(Long carId) {
        return reviewRepository.findAverageRatingByCarId(carId);
    }
}
