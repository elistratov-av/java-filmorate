package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    // GET /directors
    @GetMapping
    public List<Director> findAll() {
        List<Director> directors = directorService.findAll();
        log.info("Получен список всех режиссеров");
        return directors;
    }

    // GET /directors/{id}
    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        Director director = directorService.get(id);
        log.info("Получен режиссер: {}", director);
        return director;
    }

    // POST /directors
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        director = directorService.create(director);
        log.info("Создан режиссер: {}", director);
        return director;
    }

    // PUT /directors
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        director = directorService.update(director);
        log.info("Изменен режиссер: {}", director);
        return director;
    }

    // DELETE /directors/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        directorService.delete(id);
        log.info("Удален режиссер с id: {}", id);
    }
}
