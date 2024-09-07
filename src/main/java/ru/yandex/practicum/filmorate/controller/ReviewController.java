package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // GET /reviews/{id}
    @GetMapping("/{id}")
    public Review get(@PathVariable int id) {
        Review review = reviewService.get(id);
        log.info("Получен отзыв: {}", review);
        return review;
    }

    // GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<Review> findAll(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") int count) {
        List<Review> reviews = reviewService.findByFilmId(filmId, count);
        log.info("Получен список отзывов на фильм с id = " + filmId + ", не более чем " + count);
        return reviews;
    }

    // POST /reviews
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        review = reviewService.create(review);
        log.info("Создан отзыв: {}", review);
        return review;
    }

    // PUT /reviews
    @PutMapping
    public Review update(@Valid @RequestBody Review newReview) {
        Review review = reviewService.update(newReview);
        log.info("Изменен отзыв: {}", review);
        return review;
    }

    // DELETE /reviews/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        reviewService.deleteById(id);
        log.info("Удален отзыв с id = \"{}\"", id);
    }

    // PUT /reviews/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id, userId);
        log.info("К отзыву с id = \"{}\" добавлен лайк пользователя с id = \"{}\"", id, userId);
    }

    // PUT /reviews/{id}/dislike/{userId}
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id, userId);
        log.info("К отзыву с id = \"{}\" добавлен дизлайк пользователя с id = \"{}\"", id, userId);
    }

    // DELETE /reviews/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLike(id, userId);
        log.info("У отзыва с id = \"{}\" удален лайк пользователя с id = \"{}\"", id, userId);
    }

    // DELETE /reviews/{id}/dislike/{userId}
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislike(id, userId);
        log.info("У отзыва с id = \"{}\" удален дизлайк пользователя с id = \"{}\"", id, userId);
    }
}
