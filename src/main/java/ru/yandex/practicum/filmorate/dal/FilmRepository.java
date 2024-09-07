package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> get(Integer id);

    List<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getTopFilms(int maxCount);

    List<Film> getFilmsLikedByUser(int userId);

    HashMap<Integer, List<Film>> getLikedFilmsByUsersIds(List<Integer> usersIds);

//    List<Integer> getUsersWithSameLikes(List<Integer> filmsId);
}
