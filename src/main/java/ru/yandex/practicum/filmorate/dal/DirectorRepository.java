package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    List<Director> findAll();

    List<Director> getByIds(List<Integer> ids);

    Optional<Director> get(Integer id);

    Director create(Director director);

    Director update(Director newDirector);

    void delete(Integer id);
}
