package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa get(@PathVariable int id) {
        Mpa mpa = mpaService.get(id);
        log.info("Получен рейтинг: {}", mpa);
        return mpa;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        List<Mpa> mpas = mpaService.findAll();
        log.info("Получен список рейтингов");
        return mpas;
    }
}
