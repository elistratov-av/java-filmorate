package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository jdbc;

    private static final int TEST_FILM1_ID = 1;
    private static final int TEST_USER_ID = 1;

    private static Film getTestFilm1() {
        return Film.builder()
                .id(TEST_FILM1_ID)
                .name("Фильм 1")
                .description("Интересный фильм")
                .releaseDate(LocalDate.of(2019, Month.MAY, 14))
                .duration(15)
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(new LinkedHashSet<>(
                        List.of(Genre.builder()
                                .id(1)
                                .name("Комедия")
                                .build())))
                .directors(new LinkedHashSet<>())
                .build();
    }

    private static Film getNewFilm() {
        return Film.builder()
                .name("Фильм NEW")
                .description("Новый фильм")
                .releaseDate(LocalDate.of(2019, Month.MAY, 14))
                .duration(15)
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(new LinkedHashSet<>(
                        List.of(Genre.builder()
                                .id(1)
                                .name("Комедия")
                                .build())))
                .build();
    }

    private static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("mail1@yandex.ru")
                .login("user1")
                .name("Иванов")
                .birthday(LocalDate.of(1972, Month.AUGUST, 3))
                .build();
    }

    @Test
    @DisplayName("get должен находить фильм по id")
    void shouldGetFilmById() {
        Optional<Film> optionalFilm = jdbc.get(TEST_FILM1_ID);

        assertThat(optionalFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm1());
    }

    @Test
    @DisplayName("findAll должен возвращать список из 3 фильмов")
    void shouldFindAllFilms() {
        List<Film> films = jdbc.findAll();

        assertThat(films)
                .hasSize(3);
    }

    @Test
    @DisplayName("create должен создать новый фильм")
    void shouldCreateFilm() {
        Film newFilm = jdbc.create(getNewFilm());

        assertNotNull("id созданного фильма не равен NULL", newFilm.getId());
    }

    @Test
    @DisplayName("update должен обновить параметры фильма")
    void shouldUpdateFilm() {
        Film testFilm = getTestFilm1();
        testFilm.setName("Тестовый фильм");
        Film film = jdbc.update(testFilm);
        assertThat(film)
                .hasFieldOrPropertyWithValue("name", "Тестовый фильм");
    }

    @Test
    @DisplayName("addLike должен добавить лайк")
    void shouldAddThenDeleteLike() {
        assertThatNoException().isThrownBy(() -> jdbc.addLike(getTestFilm1(), getTestUser()));
    }

    @Test
    @DisplayName("deleteLike должен удалить лайк")
    void shouldDeleteLike() {
        assertThatNoException().isThrownBy(() -> jdbc.deleteLike(getTestFilm1(), getTestUser()));
    }

    @Test
    @DisplayName("getTopFilms должен возвращать список топ фильмов")
    void getTopFilms() {
        List<Film> films = jdbc.getTopFilms(10);

        assertThat(films)
                .hasSize(1);
    }
}
