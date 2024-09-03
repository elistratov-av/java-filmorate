package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcUserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepository")
class JdbcUserRepositoryTest {
    private final JdbcUserRepository jdbc;

    private static final int TEST_USER1_ID = 1;
    private static final int TEST_USER2_ID = 2;
    private static final int TEST_USER3_ID = 3;

    private static User getTestUser1() {
        return User.builder()
                .id(TEST_USER1_ID)
                .email("mail1@yandex.ru")
                .login("user1")
                .name("Иванов")
                .birthday(LocalDate.of(1972, Month.AUGUST, 3))
                .build();
    }

    private static User getTestUser2() {
        return User.builder()
                .id(TEST_USER2_ID)
                .email("mail2@yandex.ru")
                .login("user2")
                .name("Петров")
                .birthday(LocalDate.of(1975, Month.OCTOBER, 9))
                .build();
    }

    private static User getTestUser3() {
        return User.builder()
                .id(TEST_USER3_ID)
                .email("mail3@yandex.ru")
                .login("user3")
                .name("Сидоров")
                .birthday(LocalDate.of(1977, Month.FEBRUARY, 15))
                .build();
    }

    private static User getNewUser() {
        return User.builder()
                .email("mail5@yandex.ru")
                .login("user5")
                .name("Агапов")
                .birthday(LocalDate.of(1972, Month.AUGUST, 3))
                .build();
    }

    @Test
    @DisplayName("get должен находить пользователя по id")
    void shouldGetUserById() {
        Optional<User> optionalUser = jdbc.get(TEST_USER1_ID);

        assertThat(optionalUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser1());
    }

    @Test
    @DisplayName("findAll должен возвращать список из 3 пользователей")
    void shouldFindAllUsers() {
        List<User> users = jdbc.findAll();

        assertThat(users)
                .hasSize(3);
    }

    @Test
    @DisplayName("create должен создать нового пользователя")
    void shouldCreateUser() {
        User newUser = jdbc.create(getNewUser());

        assertNotNull("id созданного пользователя не равен NULL", newUser.getId());
    }

    @Test
    @DisplayName("update должен обновить параметры пользователя")
    void shouldUpdateUser() {
        User testUser = getTestUser1();
        testUser.setName("Остроградский Павел Петрович");
        User user = jdbc.update(testUser);
        assertThat(user)
                .hasFieldOrPropertyWithValue("name", "Остроградский Павел Петрович");
    }

    @Test
    @DisplayName("addFriend должен добавить пользователя в друзья")
    void shouldAddFriend() {
        User user1 = getTestUser1();
        User user3 = getTestUser3();
        List<User> friends = jdbc.getFriends(user3);
        assertThat(friends)
                .isEmpty();
        assertThatNoException().isThrownBy(() -> jdbc.addFriend(user3, user1));
        friends = jdbc.getFriends(user3);
        assertThat(friends)
                .hasSize(1);
    }

    @Test
    @DisplayName("deleteFriend должен удалить пользователя из друзей")
    void shouldDeleteFriend() {
        User user1 = getTestUser1();
        User user3 = getTestUser3();
        List<User> friends = jdbc.getFriends(user1);
        assertThat(friends)
                .hasSize(2);
        assertThatNoException().isThrownBy(() -> jdbc.deleteFriend(user1, user3));
        friends = jdbc.getFriends(user1);
        assertThat(friends)
                .hasSize(1);
    }

    @Test
    @DisplayName("getFriends должен вернуть двух пользователей")
    void shouldGetFriends() {
        User user1 = getTestUser1();

        List<User> friends = jdbc.getFriends(user1);
        assertThat(friends)
                .hasSize(2);
    }

    @Test
    @DisplayName("getMutualFriends должен найти 1 общего друга")
    void shouldGetMutualFriends() {
        User user1 = getTestUser1();
        User user2 = getTestUser2();

        List<User> friends = jdbc.getMutualFriends(user1, user2);
        assertThat(friends)
                .hasSize(1);
    }
}
