package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Qualifier("jdbcUserRepository")
    private final UserRepository userRepository;

    private final FilmRepository filmRepository;

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

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        if (userId < 0) {
            throw new ValidationException("Идентификатор пользователя должен быть положительным числом");
        }

        // Фильмы, которые поставили лайк пользователь X, делавший запрос
        List<Film> filmsLikedByUser = filmRepository.getFilmsLikedByUser(userId);

        Set<Integer> filmsIdsLikedByUser = filmsLikedByUser.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        // Пользователи, которые поставили лайк те же самые фильмы, что и пользователь X
        List<User> usersThatLikedSameFilms = userRepository.getUsersWithSameLikes(filmsIdsLikedByUser);
        List<Integer> usersIds = usersThatLikedSameFilms.stream()
                .map(User::getId)
                .toList();
        HashMap<Integer, List<Film>> foundUsersAllLikedFilms = filmRepository.getLikedFilmsByUsersIds(usersIds);


        // Фильмы, которые не поставили лайк пользователь X, делавший запрос
        List<Film> recommendedFilms = new ArrayList<>();

        for (Map.Entry<Integer, List<Film>> entry : foundUsersAllLikedFilms.entrySet()) {
            List<Film> likedByOtherUser = entry.getValue();

            for (Film film : likedByOtherUser) {
                if (!filmsLikedByUser.contains(film)) {
                    recommendedFilms.add(film);
                }
            }
        }
        return recommendedFilms;
    }

}
