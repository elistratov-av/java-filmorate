package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    @Qualifier("jdbcFilmRepository")
    private final FilmRepository filmRepository;
    @Qualifier("jdbcUserRepository")
    private final UserRepository userRepository;

    @Override
    public Review get(int id) {
        return reviewRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    @Override
    public List<Review> findByFilmId(Integer filmId, int count) {
        if (filmId == null) {
            return reviewRepository.findAll(count);
        } else {
            filmRepository.get(filmId)
                    .orElseThrow(() -> new ValidationException("Фильм с id = " + filmId + " не найден"));
            return reviewRepository.findByFilmId(filmId, count);
        }
    }

    private void validate(Review review, final boolean checkId) {
        if (checkId) {
            reviewRepository.get(review.getReviewId())
                    .orElseThrow(() -> new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден"));
        }
        filmRepository.get(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден"));
        userRepository.get(review.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден"));
    }

    @Override
    public Review create(Review review) {
        validate(review, false);
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review newReview) {
        validate(newReview, true);
        return reviewRepository.update(newReview);
    }

    @Override
    public void deleteById(int id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public void addLike(int id, int userId) {
        Review review = reviewRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewRepository.addLike(review, user);
    }

    @Override
    public void addDislike(int id, int userId) {
        Review review = reviewRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewRepository.addDislike(review, user);
    }

    @Override
    public void deleteLike(int id, int userId) {
        Review review = reviewRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewRepository.deleteLike(review, user);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        Review review = reviewRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        reviewRepository.deleteDislike(review, user);
    }
}
