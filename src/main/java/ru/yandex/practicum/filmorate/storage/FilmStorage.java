package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> get(int id);

    List<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getTopFilms(int maxCount);
}
