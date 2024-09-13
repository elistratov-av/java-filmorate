package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(JdbcMpaRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcMpaRepository")
class JdbcMpaRepositoryTest {
    private static final int TEST_MPA_ID = 1;
    private final JdbcMpaRepository jdbc;

    private static Mpa getTestMpa() {
        return Mpa.builder()
                .id(TEST_MPA_ID)
                .name("G")
                .build();
    }

    @Test
    @DisplayName("get должен находить рейтинг по id")
    void shouldGetGenreById() {
        Optional<Mpa> optionalMpa = jdbc.get(TEST_MPA_ID);

        assertThat(optionalMpa)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestMpa());
    }

    @Test
    @DisplayName("findAll должен вернуть список из 5 рейтингов")
    void findAll() {
        List<Mpa> mpas = jdbc.findAll();

        assertThat(mpas)
                .hasSize(5);
    }
}
