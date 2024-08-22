package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    @Qualifier("jdbcFilmRepository")
    private final FilmRepository filmRepository;
    @Qualifier("jdbcUserRepository")
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Override
    public Film get(int id) {
        return filmRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film create(Film film) {
        Mpa mpa = null;
        if (film.getMpa() != null) {
            Integer mpaId = film.getMpa().getId();
            mpa = mpaRepository.get(mpaId)
                    .orElseThrow(() -> new ValidationException("Рейтинг с id = " + mpaId + " не найден"));
        }

        List<Genre> genres = null;
        if (film.getGenres() != null) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            genres = genreRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new ValidationException("Жанры не найдены");
            }
        }

        return filmRepository.create(Film.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(mpa)
                .genres(genres != null ? new LinkedHashSet<>(genres) : null)
                .build());
    }

    @Override
    public Film update(Film newFilm) {
        final Film f = filmRepository.get(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден"));

        Mpa mpa = null;
        if (newFilm.getMpa() != null) {
            Integer mpaId = newFilm.getMpa().getId();
            mpa = mpaRepository.get(mpaId)
                    .orElseThrow(() -> new ValidationException("Рейтинг с id = " + mpaId + " не найден"));
        }

        List<Genre> genres = null;
        if (newFilm.getGenres() != null) {
            final List<Integer> genreIds = newFilm.getGenres().stream().map(Genre::getId).toList();
            genres = genreRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new ValidationException("Жанры не найдены");
            }
        }

        f.setName(newFilm.getName());
        f.setDescription(newFilm.getDescription());
        f.setReleaseDate(newFilm.getReleaseDate());
        f.setDuration(newFilm.getDuration());
        f.setMpa(mpa);
        f.setGenres(genres != null ? new LinkedHashSet<>(genres) : null);

        return filmRepository.update(f);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmRepository.addLike(film, user);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmRepository.deleteLike(film, user);
    }

    @Override
    public List<Film> getTopFilms(int maxCount) {
        return filmRepository.getTopFilms(maxCount);
    }
}
