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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.impl.FilmValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @GetMapping("/{id}")
    public Film get(@PathVariable int id) {
        Film film = filmService.get(id);
        log.info("Получен фильм: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        List<Film> films = filmService.findAll();
        log.info("Получен список фильмов");
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        filmValidator.validate(film, false);
        film = filmService.create(film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        filmValidator.validate(newFilm, true);
        Film film = filmService.update(newFilm);
        log.info("Изменен фильм: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("К фильму с id = \"{}\" добавлен лайк пользователя с id = \"{}\"", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
        log.info("У фильма с id = \"{}\" удален лайк пользователя с id = \"{}\"", id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") int count,
                                        @RequestParam(required = false) Integer genreId,
                                        @RequestParam(required = false) Integer year) {
        List<Film> topFilms;
        if (genreId != null || year != null) {
            topFilms = filmService.getTopFilms(count, genreId, year);
        } else {
            topFilms = filmService.getTopFilms(count);
        }
        log.info("Получен список популярных фильмов: {} ", topFilms.size());
        return topFilms;
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirectorId(@PathVariable int directorId,
                                                 @RequestParam(value = "sortBy") String sortBy) {
        List<Film> directorFilms = filmService.getDirectorFilms(directorId, sortBy);
        log.info("Получены фильм режиссера с id: {} отсортированных по {}", directorId, sortBy);
        return directorFilms;
    }

    // DELETE /films/{filmId}
    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
        log.info("Фильм с id = \"{}\" удален", filmId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        List<Film> films = filmService.searchFilms(query, by);
        log.info("Поиск фильмов по запросу: {} и параметру: {}", query, by);
        return films;
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam(value = "userId") int userId,
                                           @RequestParam(value = "friendId") int friendId) {
        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Получены общие фильмы пользователя id: {} с пользователем {}", userId, friendId);
        return commonFilms;
    }
}
