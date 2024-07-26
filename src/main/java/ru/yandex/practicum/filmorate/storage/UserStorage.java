package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> get(int id);

    List<User> findAll();

    User create(User user);

    User update(User newUser);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getFriends(User user);

    List<User> getMutualFriends(User user, User other);
}
