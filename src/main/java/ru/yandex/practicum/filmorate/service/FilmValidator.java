package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Service
public class FilmValidator {
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    public void validate(Film film, final boolean checkId) {
        if (checkId && film.getId() == null)
            throw new ValidationException("Id должен быть указан");
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY))
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
    }
}
