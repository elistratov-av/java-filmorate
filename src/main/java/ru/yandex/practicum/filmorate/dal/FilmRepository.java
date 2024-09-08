package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    Optional<Film> get(Integer id);

    List<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getTopFilms(int maxCount);

    List<Film> getFilmsByDirector(Integer directorId);

    List<Film> getDirectorFilmsByLikes(Integer directorId);

    Set<Integer> getFilmsLikedByUser(int userId);

    HashMap<Integer, List<Film>> getLikedFilmsByUsersIds(Set<Integer> usersIds);

}
