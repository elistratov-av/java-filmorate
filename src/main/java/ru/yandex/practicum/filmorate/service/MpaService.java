package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    Mpa get(int id);

    List<Mpa> findAll();
}
