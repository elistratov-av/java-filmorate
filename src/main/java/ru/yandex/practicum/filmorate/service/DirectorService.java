package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director get(int id);

    List<Director> findAll();

    Director create(Director director);

    Director update(Director newDirector);

    void delete(int id);
}
