package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User get(int id) {
        return userStorage.get(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.addFriend(user, friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.deleteFriend(user, friend);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return userStorage.getFriends(user);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User other = userStorage.get(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherId + " не найден"));
        return userStorage.getMutualFriends(user, other);
    }
}
