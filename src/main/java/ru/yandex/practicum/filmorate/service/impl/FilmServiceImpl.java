package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    private final FeedRepository feedRepository;
    private final DirectorRepository directorRepository;
    private final ReviewRepository reviewRepository;

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

        List<Director> directors = null;
        if (film.getDirectors() != null) {
            final List<Integer> directorIds = film.getDirectors().stream().map(Director::getId).toList();
            directors = directorRepository.getByIds(directorIds);
            if (directorIds.size() != directors.size()) {
                throw new ValidationException("Режиссеры не найдены");
            }
        }

        return filmRepository.create(Film.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(mpa)
                .genres(genres != null ? new LinkedHashSet<>(genres) : null)
                .directors(directors != null ? new LinkedHashSet<>(directors) : null)
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

        List<Genre> genres = List.of();
        if (newFilm.getGenres() != null) {
            final List<Integer> genreIds = newFilm.getGenres().stream().map(Genre::getId).toList();
            genres = genreRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new ValidationException("Жанры не найдены");
            }
        }

        List<Director> directors = List.of();
        if (newFilm.getDirectors() != null) {
            final List<Integer> directorIds = newFilm.getDirectors().stream().map(Director::getId).toList();
            directors = directorRepository.getByIds(directorIds);
            if (directorIds.size() != directors.size()) {
                throw new ValidationException("Режиссеры не найдены");
            }
        }

        f.setName(newFilm.getName());
        f.setDescription(newFilm.getDescription());
        f.setReleaseDate(newFilm.getReleaseDate());
        f.setDuration(newFilm.getDuration());
        f.setMpa(mpa);
        f.setGenres(new LinkedHashSet<>(genres));
        f.setDirectors(new LinkedHashSet<>(directors));

        return filmRepository.update(f);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmRepository.addLike(film, user);
        addLikeFeed(userId, filmId, Feed.Operation.ADD);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmRepository.deleteLike(film, user);
        addLikeFeed(userId, filmId, Feed.Operation.REMOVE);
    }

    @Override
    public List<Film> getTopFilms(int maxCount) {
        return filmRepository.getTopFilms(maxCount);
    }

    @Override
    public List<Film> getTopFilms(int count, Integer genreId, Integer year) {
        return filmRepository.getTopFilms(count, genreId, year);
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        List<Film> films = null;
        if ("likes".equalsIgnoreCase(sortBy)) {
            films = filmRepository.getDirectorFilmsByLikes(directorId);
        } else if ("year".equalsIgnoreCase(sortBy)) {
            films = filmRepository.getFilmsByDirector(directorId);
        }
        if (films == null) {
            throw new NotFoundException("Режиссер с id = " + directorId + " не найден");
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userRepository.get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        return filmRepository.getCommonFilms(user, friend);
    }

    private void addLikeFeed(Integer userId, Integer filmId, Feed.Operation operation) {
        feedRepository.create(new Feed(userId, filmId, Feed.EventType.LIKE, operation));
    }

    @Override
    public void deleteFilmById(int filmId) {
        filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));

        feedRepository.deleteFilmFeed(filmId);
        filmRepository.deleteFilmDirectors(filmId);
        filmRepository.deleteFilmLikes(filmId);
        filmRepository.deleteFilmGenres(filmId);
        reviewRepository.deleteFilmReviewLikes(filmId);
        reviewRepository.deleteFilmReviews(filmId);
        filmRepository.deleteFilmById(filmId);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        return filmRepository.searchFilms(query, by);
    }
}
