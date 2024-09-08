package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    Optional<User> get(Integer id);

    List<User> findAll();

    User create(User user);

    User update(User newUser);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getFriends(User user);

    List<User> getMutualFriends(User user, User other);

    void deleteUserById(int userId);

    void deleteUserLikes(int userId);

    void deleteUserFriends(int userId);

    Set<Integer> getUsersWithSameLikes(Set<Integer> filmsId);
}
