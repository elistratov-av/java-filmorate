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
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        List<Film> topFilms = filmService.getTopFilms(count);
        log.info("Получен список популярных фильмов: {} ", count);
        return topFilms;
    }
}
