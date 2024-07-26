package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int idCounter = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmUserIds = new HashMap<>();

    protected int nextId() {
        return ++idCounter;
    }

    @Override
    public Optional<Film> get(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        // формируем дополнительные данные
        film.setId(nextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = get(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден"));

        // если фильм найден и все условия соблюдены, обновляем его содержимое
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }

    @Override
    public void addLike(Film film, User user) {
        Set<Integer> filmUsers = filmUserIds.computeIfAbsent(film.getId(), id -> new HashSet<>());
        filmUsers.add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        Set<Integer> filmUsers = filmUserIds.computeIfAbsent(film.getId(), id -> new HashSet<>());
        filmUsers.remove(user.getId());
    }

    @Override
    public List<Film> getTopFilms(int maxCount) {
        List<Film> topFilms = filmUserIds.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().size()))
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(maxCount)
                .map(e -> films.get(e.getKey()))
                .collect(Collectors.toList());
        return topFilms;
    }
}
