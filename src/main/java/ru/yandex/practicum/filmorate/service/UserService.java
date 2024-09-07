package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User get(int id);

    List<User> findAll();

    User create(User user);

    User update(User newUser);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherId);

    List<Feed> getFeed(int userId);
}
