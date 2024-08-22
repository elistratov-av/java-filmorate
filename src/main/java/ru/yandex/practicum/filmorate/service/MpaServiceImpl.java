package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Mpa get(int id) {
        return mpaRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }

    @Override
    public List<Mpa> findAll() {
        return mpaRepository.findAll();
    }
}
