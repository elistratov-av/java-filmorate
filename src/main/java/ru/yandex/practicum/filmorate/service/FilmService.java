package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film get(int id);

    List<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getTopFilms(int maxCount);

    List<Film> getDirectorFilms(int directorId, String sortBy);
}
