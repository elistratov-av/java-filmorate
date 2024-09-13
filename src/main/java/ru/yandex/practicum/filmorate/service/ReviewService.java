package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review get(int id);

    List<Review> findByFilmId(Integer filmId, int count);

    Review create(Review review);

    Review update(Review newReview);

    void deleteById(int id);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);
}
