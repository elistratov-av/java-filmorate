package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Genre get(int id) {
        return genreRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
}
