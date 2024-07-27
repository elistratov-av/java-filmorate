package ru.yandex.practicum.filmorate.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int idCounter = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> userFriendIds = new HashMap<>();

    protected int nextId() {
        return ++idCounter;
    }

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        // формируем дополнительные данные
        user.setId(nextId());
        if (StringUtils.isBlank(user.getName()))
            user.setName(user.getLogin());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        // проверяем необходимые условия
        User oldUser = get(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));

        if (StringUtils.isBlank(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        // если фильм найден и все условия соблюдены, обновляем его содержимое
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    @Override
    public void addFriend(User user, User friend) {
        Set<Integer> userFriends = userFriendIds.computeIfAbsent(user.getId(), id -> new HashSet<>());
        userFriends.add(friend.getId());

        Set<Integer> friendFriends = userFriendIds.computeIfAbsent(friend.getId(), id -> new HashSet<>());
        friendFriends.add(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        Set<Integer> userFriends = userFriendIds.computeIfAbsent(user.getId(), id -> new HashSet<>());
        userFriends.remove(friend.getId());

        Set<Integer> friendFriends = userFriendIds.computeIfAbsent(friend.getId(), id -> new HashSet<>());
        friendFriends.remove(user.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        return userFriendIds.computeIfAbsent(user.getId(), id -> new HashSet<>())
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(User user, User other) {
        Set<Integer> userFriends = userFriendIds.computeIfAbsent(user.getId(), id -> new HashSet<>());
        Set<Integer> otherFriends = userFriendIds.computeIfAbsent(other.getId(), id -> new HashSet<>());

        Set<Integer> mutualFriends = new HashSet<>(userFriends);
        mutualFriends.retainAll(otherFriends);
        return mutualFriends
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
