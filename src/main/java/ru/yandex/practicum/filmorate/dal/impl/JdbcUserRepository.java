package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("jdbcUserRepository")
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String FIND_ALL_QUERY = "SELECT user_id, email, login, user_name, birthday FROM users";
    private static final String GET_BY_ID_QUERY =
            "SELECT user_id, email, login, user_name, birthday FROM users u WHERE u.user_id = :id";
    private static final String INSERT_QUERY =
            "INSERT INTO users (email, login, user_name, birthday) VALUES(:email, :login, :user_name, :birthday)";
    private static final String UPDATE_QUERY = """
            UPDATE users SET email = :email, login = :login, user_name = :user_name, birthday = :birthday
            WHERE user_id = :user_id""";
    private static final String ADD_FRIEND_QUERY =
            "MERGE INTO friends (user_id, friend_id) VALUES(:user_id, :friend_id)";
    private static final String DELETE_FRIEND_QUERY =
            "DELETE FROM friends WHERE user_id = :user_id AND friend_id = :friend_id";
    private static final String GET_FRIENDS_QUERY = """
            SELECT
            	u.user_id, u.email, u.login, u.user_name, u.birthday
            FROM
            	friends fr
            JOIN users u ON
            	fr.friend_id = u.user_id
            WHERE
            	fr.user_id = :user_id""";
    private static final String GET_MUTUAL_FRIENDS_QUERY = """
            SELECT
            	u.user_id, u.email, u.login, u.user_name, u.birthday
            FROM
            	friends fr
            JOIN users u ON
            	fr.friend_id = u.user_id
            WHERE
            	fr.user_id = :user1_id
            	AND fr.friend_id IN (
            	SELECT
            		fr3.friend_id
            	FROM
            		friends fr3
            	WHERE
            		fr3.user_id = :user2_id)""";
    private static final String DELETE_USER_BY_ID_QUERY = """
            DELETE FROM users
            WHERE user_id = :user_id""";
    private static final String DELETE_USER_LIKES_QUERY = """
            DELETE FROM likes
            WHERE user_id = :user_id""";
    private static final String DELETE_USER_FRIENDS_QUERY = """
            DELETE FROM friends
            WHERE user_id = :user_id OR friend_id = :user_id""";

    private static final String GET_USERS_WITH_COMMON_FILMS_BY_FILMS = """
            SELECT l.user_id
            FROM
                likes AS l
            INNER JOIN users AS u ON
                l.user_id = u.user_id
            WHERE
                l.film_id IN (:films_id)
            GROUP BY
                l.user_id
            ORDER
                BY COUNT(l.film_id) DESC
            LIMIT 10
            """;

    // endregion

    // region Mapper

    private static User mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        Date birthday = rs.getDate("birthday");
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(birthday != null ? birthday.toLocalDate() : null)
                .build();
    }

    private static Set<Integer> mapSetToUserIds(ResultSet rs) throws SQLException {
        Set<Integer> usersIds = new HashSet<>();

        while (rs.next()) {
            Integer userId = rs.getInt("user_id");
            usersIds.add(userId);
        }
        return usersIds;
    }

    // endregion

    @Override
    public Optional<User> get(Integer id) {
        try {
            if (id == null) return Optional.empty();

            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    JdbcUserRepository::mapRowTo));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY,
                JdbcUserRepository::mapRowTo);
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_QUERY,
                new MapSqlParameterSource("email", user.getEmail())
                        .addValue("login", user.getLogin())
                        .addValue("user_name", user.getName())
                        .addValue("birthday", user.getBirthday()),
                gkh);
        user.setId(gkh.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public User update(User newUser) {
        jdbc.update(UPDATE_QUERY,
                new MapSqlParameterSource("email", newUser.getEmail())
                        .addValue("login", newUser.getLogin())
                        .addValue("user_name", newUser.getName())
                        .addValue("birthday", newUser.getBirthday())
                        .addValue("user_id", newUser.getId()));
        return newUser;
    }

    @Override
    public void addFriend(User user, User friend) {
        jdbc.update(ADD_FRIEND_QUERY,
                new MapSqlParameterSource("user_id", user.getId())
                        .addValue("friend_id", friend.getId()));
    }

    @Override
    public void deleteFriend(User user, User friend) {
        jdbc.update(DELETE_FRIEND_QUERY,
                new MapSqlParameterSource("user_id", user.getId())
                        .addValue("friend_id", friend.getId()));
    }

    @Override
    public List<User> getFriends(User user) {
        return jdbc.query(GET_FRIENDS_QUERY,
                new MapSqlParameterSource("user_id", user.getId()),
                JdbcUserRepository::mapRowTo);
    }

    @Override
    public List<User> getMutualFriends(User user, User other) {
        return jdbc.query(GET_MUTUAL_FRIENDS_QUERY,
                new MapSqlParameterSource("user1_id", user.getId())
                        .addValue("user2_id", other.getId()),
                JdbcUserRepository::mapRowTo);
    }

    @Override
    public void deleteUserById(int userId) {
        jdbc.update(DELETE_USER_BY_ID_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public void deleteUserLikes(int userId) {
        jdbc.update(DELETE_USER_LIKES_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public void deleteUserFriends(int userId) {
        jdbc.update(DELETE_USER_FRIENDS_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public Set<Integer> getUsersWithSameLikes(Set<Integer> films) {

        return jdbc.query(GET_USERS_WITH_COMMON_FILMS_BY_FILMS,
                new MapSqlParameterSource("films_id", films),
                JdbcUserRepository::mapSetToUserIds);
    }

}
