package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.impl.UserValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        User user = userService.get(id);
        log.info("Получен пользователь: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        List<User> users = userService.findAll();
        log.info("Получен список пользователей");
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        // проверяем выполнение необходимых условий
        userValidator.validate(user, false);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userService.create(user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        userValidator.validate(newUser, true);
        User user = userService.update(newUser);
        log.info("Изменен пользователь: {}", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с id = \"{}\" добавлен в друзья к пользователю с id = \"{}\"", friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь с id = \"{}\" удален из друзей пользователя с id = \"{}\"", friendId, id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        List<User> friends = userService.getFriends(id);
        log.info("Получен список друзей для пользователя с id = \"{}\"", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        List<User> friends = userService.getMutualFriends(id, otherId);
        log.info("Получен список общих друзей для пользователей с id = \"{}\" и otherId = \"{}\"", id, otherId);
        return friends;
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeed(@PathVariable int id) {
        List<Feed> feed = userService.getFeed(id);
        log.info("Получен список последних событий на платформе друзей пользователя с id = \"{}\"", id);
        return feed;
    }

    // DELETE /users/{userId}
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUserById(userId);
        log.info("Пользователь с id = \"{}\" удален", userId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendedFilms(@PathVariable int id) {

            List<Film> recommendedFilms = userService.getRecommendedFilms(id);
            log.info("Получен список рекомендации для пользователя с id = \"{}\"", id);
            return recommendedFilms;
    }
}
