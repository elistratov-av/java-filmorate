package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre get(int id);

    List<Genre> findAll();
}
