package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Optional<Review> get(Integer id);

    List<Review> findByFilmId(int filmId, int count);

    List<Review> findAll(int count);

    Review create(Review review);

    Review update(Review newReview);

    void deleteById(int id);

    void addLike(Review review, User user);

    void addDislike(Review review, User user);

    void deleteLike(Review review, User user);

    void deleteDislike(Review review, User user);
}
