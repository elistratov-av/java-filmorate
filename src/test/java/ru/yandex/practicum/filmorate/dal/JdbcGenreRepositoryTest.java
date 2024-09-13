package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(JdbcGenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcGenreRepository")
class JdbcGenreRepositoryTest {
    private static final int TEST_GENRE_ID = 1;
    private final JdbcGenreRepository jdbc;

    private static Genre getTestGenre() {
        return Genre.builder()
                .id(TEST_GENRE_ID)
                .name("Комедия")
                .build();
    }

    @Test
    @DisplayName("get должен находить жанр по id")
    void shouldGetGenreById() {
        Optional<Genre> optionalGenre = jdbc.get(TEST_GENRE_ID);

        assertThat(optionalGenre)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }

    @Test
    @DisplayName("findAll должен вернуть список из 6 жанров")
    void findAll() {
        List<Genre> genres = jdbc.findAll();

        assertThat(genres)
                .hasSize(6);
    }
}
