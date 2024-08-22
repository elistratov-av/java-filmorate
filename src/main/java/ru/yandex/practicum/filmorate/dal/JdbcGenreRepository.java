package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String GET_BY_ID_QUERY = "SELECT * FROM genres g WHERE g.genre_id = :id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String GET_BY_IDS_QUERY = "SELECT * FROM genres g WHERE g.genre_id IN (:ids)";

    // endregion

    // region Mapper

    private static Genre mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    // endregion

    @Override
    public Optional<Genre> get(Integer id) {
        try {
            if (id == null) return Optional.empty();

            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    JdbcGenreRepository::mapRowTo));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, JdbcGenreRepository::mapRowTo);
    }

    @Override
    public List<Genre> getByIds(List<Integer> ids) {
        return jdbc.query(GET_BY_IDS_QUERY,
                new MapSqlParameterSource("ids", ids),
                JdbcGenreRepository::mapRowTo);
    }
}
