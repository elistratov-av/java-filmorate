package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Qualifier("jdbcUserRepository")
    private final UserRepository userRepository;

    @Override
    public User get(int id) {
        return userRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User update(User newUser) {
        userRepository.get(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));
        return userRepository.update(newUser);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userRepository.get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userRepository.addFriend(user, friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userRepository.get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userRepository.deleteFriend(user, friend);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return userRepository.getFriends(user);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User other = userRepository.get(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherId + " не найден"));
        return userRepository.getMutualFriends(user, other);
    }
}
