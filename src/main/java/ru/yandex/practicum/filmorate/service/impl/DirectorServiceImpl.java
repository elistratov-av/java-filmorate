package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Override
    public Director get(int id) {
        return directorRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден"));
    }

    @Override
    public List<Director> findAll() {
        return directorRepository.findAll();
    }

    @Override
    public Director create(Director director) {
        return directorRepository.create(director);
    }

    @Override
    public Director update(Director newDirector) {
        directorRepository.get(newDirector.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + newDirector.getId() + " не найден"));
        return directorRepository.update(newDirector);
    }

    @Override
    public void delete(int id) {
        directorRepository.delete(id);
    }
}
